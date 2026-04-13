package br.sptrans.scd.auth.adapter.in.web.filter;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.sptrans.scd.audit.application.port.in.AuditUseCase;
import br.sptrans.scd.audit.domain.AuditEvent;
import br.sptrans.scd.audit.domain.AuditEventType;
import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.auth.application.port.out.UserSessionRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.port.out.TokenValidatorPort;
import br.sptrans.scd.auth.domain.session.UserSession;
import br.sptrans.scd.shared.audit.AuditContext;
import br.sptrans.scd.shared.exception.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Adapter de entrada: Filtro JWT + validação de sessão.
 *
 * Responsabilidades:
 *  1. Extrair o token do header Authorization
 *  2. Validar o token via port (TokenValidatorPort)
 *  3. Carregar o usuário via port (UserPersistencePort)
 *  4. Verificar status do usuário (ativo/bloqueado)
 *  5. Extrair session_id do token e validar sessão no banco/cache
 *  6. Popular o SecurityContextHolder
 *
 * Vive em adapter/in/web/filter — não contém lógica de negócio.
 * Depende APENAS de ports (interfaces), nunca de adapters concretos.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final TokenValidatorPort tokenValidator;
    private final UserPersistencePort userRepository;
    private final UserSessionRepository sessionRepository;
    private final AuthorityBuilderAdapter authorityBuilder;
    private final ObjectMapper objectMapper;
    private final AuditUseCase auditUseCase;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            String login = tokenValidator.validateAndGetSubject(token);

            if (login == null || login.isBlank()) {
                writeUnauthorized(response, "TOKEN_INVALID", "Token inválido ou expirado", request);
                return;
            }

            User user = userRepository.findByCodLogin(login).orElse(null);

            if (user == null) {
                writeUnauthorized(response, "USER_NOT_FOUND", "Usuário não encontrado", request);
                return;
            }

            if (user.isBlocked()) {
                AuditContext.Data ctx = AuditContext.get();
                auditUseCase.audit(AuditEvent.preAuth(
                        AuditEventType.REQUEST_UNAUTHORIZED,
                        ctx.ipAddress, ctx.userAgent,
                        "{\"reason\":\"ACCOUNT_BLOCKED\"}"));
                writeForbidden(response, "ACCOUNT_BLOCKED",
                    "Conta bloqueada por múltiplas tentativas de login", request);
                return;
            }

            if (!user.isActived()) {
                AuditContext.Data ctxInactive = AuditContext.get();
                auditUseCase.audit(AuditEvent.preAuth(
                        AuditEventType.REQUEST_UNAUTHORIZED,
                        ctxInactive.ipAddress, ctxInactive.userAgent,
                        "{\"reason\":\"USER_INACTIVE\"}"));
                writeForbidden(response, "USER_INACTIVE", "Usuário inativo", request);
                return;
            }

            // Valida sessão: extrai session_id do token e verifica no banco/cache
            String sessionId = tokenValidator.extractSessionId(token);
            if (sessionId != null) {
                UserSession session = sessionRepository.findBySessionId(sessionId).orElse(null);
                if (session == null) {
                    writeUnauthorized(response, "SESSION_NOT_FOUND", "Sessão não encontrada", request);
                    return;
                }
                if (!session.isAtiva()) {
                    writeUnauthorized(response, "SESSION_INVALID",
                            "Sessão expirada ou revogada", request);
                    return;
                }
            }

            List<GrantedAuthority> authorities = authorityBuilder.buildAuthorities(user);
            var authentication = new UsernamePasswordAuthenticationToken(
                user.getCodLogin(),
                null,
                authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Enriquece AuditContext + MDC com dados do usuário autenticado
            AuditContext.setAuth(user.getIdUsuario(), sessionId);
            org.slf4j.MDC.put("userId",    String.valueOf(user.getIdUsuario()));
            org.slf4j.MDC.put("sessionId", sessionId != null ? sessionId : "-");
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path   = request.getRequestURI();
        String method = request.getMethod();
        boolean ignore = method.equalsIgnoreCase("OPTIONS")
            || path.startsWith("/api/v1/auth/login")
            || path.startsWith("/api/v1/auth/forgot-password")
            || path.startsWith("/api/v1/auth/reset-password")
            || path.startsWith("/actuator/health")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs");
        log.info("JwtAuthFilter.shouldNotFilter: path={}, ignore={}", path, ignore);
        return ignore;
    }

    private void writeUnauthorized(HttpServletResponse response,
                                   String errorCode, String message,
                                   HttpServletRequest request) throws IOException {
        ErrorResponse body = new ErrorResponse(
            401, "Unauthorized", message, errorCode, request.getRequestURI());
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private void writeForbidden(HttpServletResponse response,
                                String errorCode, String message,
                                HttpServletRequest request) throws IOException {
        ErrorResponse body = new ErrorResponse(
            403, "Forbidden", message, errorCode, request.getRequestURI());
        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}


// /**
//  * Adapter de entrada: Filtro JWT.
//  *
//  * Responsabilidades:
//  *  1. Extrair o token do header Authorization
//  *  2. Validar o token via port (TokenValidatorPort)
//  *  3. Carregar o usuário via port (UserPersistencePort)
//  *  4. Verificar status do usuário (ativo/bloqueado)
//  *  5. Popular o SecurityContextHolder
//  *
//  * Vive em adapter/in/web/filter — não contém lógica de negócio.
//  * Depende APENAS de ports (interfaces), nunca de adapters concretos.
//  */
// @Component
// @RequiredArgsConstructor
// public class JwtAuthFilter extends OncePerRequestFilter {
//     private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

//     private final TokenValidatorPort tokenValidator;
//     private final UserPersistencePort userRepository;
//     private final AuthorityBuilderAdapter authorityBuilder;
//     private final ObjectMapper objectMapper;



//     @Override
//     protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                     @NonNull HttpServletResponse response,
//                                     @NonNull FilterChain filterChain)
//             throws ServletException, IOException {

//         String token = extractToken(request);

//         if (token != null) {
//             String login = tokenValidator.validateAndGetSubject(token);

//             // Token inválido ou expirado → login vazio
//             if (login == null || login.isBlank()) {
//                 writeUnauthorized(response, "TOKEN_INVALID", "Token inválido ou expirado", request);
//                 return;
//             }

//             User user = userRepository.findByCodLogin(login).orElse(null);

//             if (user == null) {
//                 writeUnauthorized(response, "USER_NOT_FOUND", "Usuário não encontrado", request);
//                 return;
//             }

//             // Usuário bloqueado — retorna 403
//             if (user.isBlocked()) {
//                 writeForbidden(response, "ACCOUNT_BLOCKED",
//                     "Conta bloqueada por múltiplas tentativas de login", request);
//                 return;
//             }

//             // Usuário inativo — retorna 403
//             if (!user.isActived()) {
//                 writeForbidden(response, "USER_INACTIVE", "Usuário inativo", request);
//                 return;
//             }

//             // Tudo ok → popula o SecurityContextHolder
//             List<GrantedAuthority> authorities = authorityBuilder.buildAuthorities(user);
//             var authentication = new UsernamePasswordAuthenticationToken(
//                 user.getCodLogin(),  // principal
//                 null,             // credentials (não necessário após autenticação)
//                 authorities
//             );
//             SecurityContextHolder.getContext().setAuthentication(authentication);
//         }

//         filterChain.doFilter(request, response);
//     }

//     /**
//      * Extrai o Bearer token do header Authorization.
//      * Retorna null se ausente ou mal-formatado.
//      */
//     private String extractToken(HttpServletRequest request) {
//         String authHeader = request.getHeader("Authorization");
//         if (authHeader != null && authHeader.startsWith("Bearer ")) {
//             return authHeader.substring(7); // Remove "Bearer "
//         }
//         return null;
//     }

//     /**
//      * Rotas públicas não passam por este filtro.
//      * Evita consulta desnecessária ao banco em /login, /health, /swagger, etc.
//      */
//     @Override
//     protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
//         String path   = request.getRequestURI();
//         String method = request.getMethod();
//         boolean ignore = method.equalsIgnoreCase("OPTIONS")
//             || path.startsWith("/api/v1/auth/login")
//             || path.startsWith("/api/v1/auth/forgot-password")
//             || path.startsWith("/api/v1/auth/reset-password")
//             || path.startsWith("/actuator/health")
//             || path.startsWith("/swagger-ui")
//             || path.startsWith("/v3/api-docs");
//         log.info("JwtAuthFilter.shouldNotFilter: path={}, ignore={}", path, ignore);
//         return ignore;
//     }

//     private void writeUnauthorized(HttpServletResponse response,
//                                    String errorCode, String message,
//                                    HttpServletRequest request) throws IOException {
//         ErrorResponse body = new ErrorResponse(
//             401, "Unauthorized", message, errorCode, request.getRequestURI());
//         response.setStatus(401);
//         response.setContentType("application/json;charset=UTF-8");
//         response.getWriter().write(objectMapper.writeValueAsString(body));
//     }

//     private void writeForbidden(HttpServletResponse response,
//                                 String errorCode, String message,
//                                 HttpServletRequest request) throws IOException {
//         ErrorResponse body = new ErrorResponse(
//             403, "Forbidden", message, errorCode, request.getRequestURI());
//         response.setStatus(403);
//         response.setContentType("application/json;charset=UTF-8");
//         response.getWriter().write(objectMapper.writeValueAsString(body));
//     }
// }