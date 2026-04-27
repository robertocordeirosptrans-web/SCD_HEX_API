package br.sptrans.scd.auth.adapter.in.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.adapter.in.rest.dto.MeResponse;
import br.sptrans.scd.auth.adapter.in.rest.dto.ResponseLogin;
import br.sptrans.scd.auth.adapter.in.rest.dto.ResponseSimple;
import br.sptrans.scd.audit.application.port.in.AuditUseCase;
import br.sptrans.scd.audit.domain.AuditEvent;
import br.sptrans.scd.audit.domain.AuditEventType;
import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.AuthComand;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.ResetPasswordComand;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.ResetRequestComand;
import br.sptrans.scd.auth.application.port.in.SessionManagementUseCase;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.port.out.TokenGeneratorPort;
import br.sptrans.scd.auth.domain.port.out.TokenValidatorPort;
import br.sptrans.scd.auth.domain.session.UserSession;
import br.sptrans.scd.shared.audit.AuditContext;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/auth")
@Tag(name = "Autenticação v1", description = "Endpoints para autenticação e gerenciamento de usuários - Versão 1")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthUseCase casoUso;
    private final TokenGeneratorPort tokenGenerator;
    private final TokenValidatorPort tokenValidator;
    private final SessionManagementUseCase sessionUseCase;
    private final AuditUseCase auditUseCase;


    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Autentica o usuário, cria sessão rastreada e retorna token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Credenciais inválidas ou usuário bloqueado/inativo")
    })
    public ResponseEntity<ResponseLogin> login(@RequestBody @Valid RequestLogin req,
                                               HttpServletRequest httpRequest) {
        log.info("REST POST /auth/login — Tentativa de autenticação");
        AuthComand auth = new AuthComand(req.login(), req.password());
        User user = casoUso.autenticar(auth);

        // Cria sessão rastreada
        String ip        = resolveClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        UserSession session = sessionUseCase.createSession(user.getIdUsuario(), ip, userAgent);

        // Embute session_id no JWT
        String jwt = tokenGenerator.generate(user, session.getIdSessao());
        return ResponseEntity.ok(new ResponseLogin(jwt));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Logout", description = "Invalida a sessão atual — o token deixa de ser aceito imediatamente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<ResponseSimple> logout(HttpServletRequest request,
                                                 Authentication authentication) {
        String token     = extractBearerToken(request);
        String sessionId = token != null ? tokenValidator.extractSessionId(token) : null;

        if (sessionId != null) {
            String codLogin = authentication.getName();
            sessionUseCase.revokeSession(sessionId, null, "LOGOUT");
            log.info("Logout realizado: login={}, sessionId={}", codLogin, sessionId);
            AuditContext.Data ctx = AuditContext.get();
            auditUseCase.audit(AuditEvent.of(
                    AuditEventType.LOGOUT,
                    ctx.userId, sessionId,
                    ctx.ipAddress, ctx.userAgent, null));
        }
        return ResponseEntity.ok(new ResponseSimple("Logout realizado com sucesso."));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Dados do usuário autenticado", description = "Retorna id, nome, perfis, permissões e grupos do usuário logado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        String codLogin = authentication.getName();
        AuthUseCase.UserContext ctx = casoUso.loadUserContext(codLogin);
        return ResponseEntity.ok(new MeResponse(ctx.id(), ctx.name(), ctx.roles(), ctx.permissions(), ctx.groups()));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Trocar senha", description = "Permite ao usuário autenticado trocar sua própria senha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ResponseSimple> changePassword(@Valid @RequestBody ResquestChangePassword req) {
        log.info("REST POST /auth/change-password — Solicitação de troca de senha");
        ResetPasswordComand reset = new ResetPasswordComand(req.token(), req.novaSenha());
        casoUso.resetPassword(reset);
        return ResponseEntity.ok(new ResponseSimple(
                "Senha redefinida com sucesso. Faça login com a nova senha."));
    }

    @PostMapping("/recovery-password")
    public ResponseEntity<ResponseSimple> recoveryPassword(@Valid @RequestBody RequestRecoveryPassword req) {
        log.info("REST POST /auth/recovery-password — Solicitação de recuperação de senha");

        ResetRequestComand cmd = new ResetRequestComand(req.email());
        casoUso.recoveryResetPassword(cmd);
        return ResponseEntity.ok(new ResponseSimple(
                "Se o e-mail estiver cadastrado, você receberá as instruções em instantes."));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    // ── DTOs ─────────────────────────────────────────────────────────────────
    public record RequestLogin(@NotBlank(message = "Login é obrigatório")
            String login,
            @NotBlank(message = "Senha é obrigatória")
            String password) {

    }


    public record RequestRecoveryPassword(
            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "E-mail deve ser válido")
            @Size(max = 40, message = "E-mail deve ter no máximo 40 caracteres")
            String email) {

    }

    public record ResquestChangePassword(
            @NotBlank(message = "Token é obrigatório")
            String token,

            @NotBlank(message = "Nova senha é obrigatória")
            @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
            String novaSenha) {

    }

}


// @RestController
// @RequiredArgsConstructor
// @RequestMapping(ApiVersionConfig.API_V1_PATH + "/auth")
// @Tag(name = "Autenticação v1", description = "Endpoints para autenticação e gerenciamento de usuários - Versão 1")
// public class AuthController {

//     private static final Logger log = LoggerFactory.getLogger(AuthController.class);

//     private final AuthUseCase casoUso;
//     private final TokenGeneratorPort tokenGenerator;


//     @PostMapping("/login")
//     @Operation(summary = "Login do usuário", description = "Autentica o usuário e retorna um token JWT com payload mínimo")
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
//         @ApiResponse(responseCode = "403", description = "Credenciais inválidas ou usuário bloqueado/inativo")
//     })
//     public ResponseEntity<ResponseLogin> login(@RequestBody @Valid RequestLogin req) {
//         log.info("REST POST /auth/login — Tentativa de autenticação");
//         AuthComand auth = new AuthComand(req.login(), req.password());
//         User user = casoUso.autenticar(auth);
//         String jwt = tokenGenerator.generate(user);
//         return ResponseEntity.ok(new ResponseLogin(jwt));
//     }

//     @GetMapping("/me")
//     @PreAuthorize("hasRole('ADMIN')")
//     @SecurityRequirement(name = "bearerAuth")
//     @Operation(summary = "Dados do usuário autenticado", description = "Retorna id, nome, perfis, permissões e grupos do usuário logado")
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
//         @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
//     })
//     public ResponseEntity<MeResponse> me(Authentication authentication) {
//         String codLogin = authentication.getName();
//         AuthUseCase.UserContext ctx = casoUso.loadUserContext(codLogin);
//         return ResponseEntity.ok(new MeResponse(ctx.id(), ctx.name(), ctx.roles(), ctx.permissions(), ctx.groups()));
//     }

//     @PostMapping("/change-password")
//     @Operation(summary = "Trocar senha", description = "Permite ao usuário autenticado trocar sua própria senha")
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
//         @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
//         @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
//     })
//     @SecurityRequirement(name = "bearerAuth")
//     public ResponseEntity<ResponseSimple> changePassword(@RequestBody ResquestChangePassword req) {
//         log.info("REST POST /auth/change-password — Solicitação de troca de senha");
//         ResetPasswordComand reset = new ResetPasswordComand(req.token(), req.novaSenha());
//         casoUso.resetPassword(reset);
//         return ResponseEntity.ok(new ResponseSimple(
//                 "Senha redefinida com sucesso. Faça login com a nova senha."));
//     }

//     @PostMapping("/recovery-password")
//     public ResponseEntity<ResponseSimple> recoveryPassword(@RequestBody RequestRecoveryPassword req) {
//         log.info("REST POST /auth/recovery-password — Solicitação de recuperação de senha");

//         ResetRequestComand cmd = new ResetRequestComand(req.email());
//         casoUso.recoveryResetPassword(cmd);
//         // Resposta genérica — não confirma se e-mail existe (segurança)
//         return ResponseEntity.ok(new ResponseSimple(
//                 "Se o e-mail estiver cadastrado, você receberá as instruções em instantes."));
//     }

//     // ── DTOs ─────────────────────────────────────────────────────────────────
//     public record RequestLogin(@NotBlank(message = "Login é obrigatório")
//             String login,
//             @NotBlank(message = "Senha é obrigatória")
//             String password) {

//     }

//     public record ResponseLogin(String token) {

//     }

//     public record MeResponse(Long id, String name, Set<String> roles, Set<String> permissions, Set<String> groups) {

//     }


//     public record ResponseSimple(String mensagem) {

//     }

//     public record RequestRecoveryPassword(String email) {

//     }

//     public record ResquestChangePassword(String token, String novaSenha) {

//     }

// }
