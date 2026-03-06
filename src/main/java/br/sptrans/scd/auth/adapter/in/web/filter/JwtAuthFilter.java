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

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.port.out.TokenValidatorPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Adapter de entrada: Filtro JWT.
 *
 * Responsabilidades:
 *  1. Extrair o token do header Authorization
 *  2. Validar o token via port (TokenValidatorPort)
 *  3. Carregar o usuário via port (UserRepositoryPort)
 *  4. Verificar status do usuário (ativo/bloqueado)
 *  5. Popular o SecurityContextHolder
 *
 * Vive em adapter/in/web/filter — não contém lógica de negócio.
 * Depende APENAS de ports (interfaces), nunca de adapters concretos.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final TokenValidatorPort tokenValidator;
    private final UserRepository userRepository;
    private final AuthorityBuilderAdapter authorityBuilder;

    public JwtAuthFilter(TokenValidatorPort tokenValidator,
                         UserRepository userRepository,
                         AuthorityBuilderAdapter authorityBuilder) {
        this.tokenValidator   = tokenValidator;
        this.userRepository   = userRepository;
        this.authorityBuilder = authorityBuilder;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            String login = tokenValidator.validateAndGetSubject(token);

            // Token inválido ou expirado → login vazio
            if (login == null || login.isBlank()) {
                writeUnauthorized(response, "TOKEN_INVALID", "Token inválido ou expirado");
                return;
            }

            User user = userRepository.findByCodLogin(login).orElse(null);

            if (user == null) {
                writeUnauthorized(response, "USER_NOT_FOUND", "Usuário não encontrado");
                return;
            }

            // Usuário bloqueado — retorna 403
            if (user.isBlocked()) {
                writeForbidden(response, "ACCOUNT_BLOCKED",
                    "Conta bloqueada por múltiplas tentativas de login");
                return;
            }

            // Usuário inativo — retorna 403
            if (!user.isActived()) {
                writeForbidden(response, "USER_INACTIVE", "Usuário inativo");
                return;
            }

            // Tudo ok → popula o SecurityContextHolder
            List<GrantedAuthority> authorities = authorityBuilder.buildAuthorities(user);
            var authentication = new UsernamePasswordAuthenticationToken(
                user.getCodLogin(),  // principal
                null,             // credentials (não necessário após autenticação)
                authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o Bearer token do header Authorization.
     * Retorna null se ausente ou mal-formatado.
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer "
        }
        return null;
    }

    /**
     * Rotas públicas não passam por este filtro.
     * Evita consulta desnecessária ao banco em /login, /health, /swagger, etc.
     */
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
                                   String errorCode, String message) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
            String.format("{\"errorCode\":\"%s\",\"message\":\"%s\"}", errorCode, message));
    }

    private void writeForbidden(HttpServletResponse response,
                                String errorCode, String message) throws IOException {
        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
            String.format("{\"errorCode\":\"%s\",\"message\":\"%s\"}", errorCode, message));
    }
}