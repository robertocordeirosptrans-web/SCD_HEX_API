package br.sptrans.scd.auth.adapter.port.in.rest;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.AuthComand;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.ResetPasswordComand;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.ResetRequestComand;
import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.port.out.TokenGeneratorPort;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/auth")
@Tag(name = "Autenticação v1", description = "Endpoints para autenticação e gerenciamento de usuários - Versão 1")
public class AuthController {

    private final AuthUseCase casoUso;
    private final TokenGeneratorPort tokenGenerator;
    private final UserRepository userRepository;

    public AuthController(AuthUseCase casoUso, TokenGeneratorPort tokenGenerator, UserRepository userRepository) {
        this.casoUso = casoUso;
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Autentica o usuário e retorna um token JWT com payload mínimo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Credenciais inválidas ou usuário bloqueado/inativo")
    })
    public ResponseEntity<ResponseLogin> login(@RequestBody @Valid RequestLogin req) {
        AuthComand auth = new AuthComand(req.login(), req.password());
        User user = casoUso.autenticar(auth);
        String jwt = tokenGenerator.generate(user);
        return ResponseEntity.ok(new ResponseLogin(jwt));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Dados do usuário autenticado", description = "Retorna id, nome, perfis e permissões do usuário logado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        String codLogin = authentication.getName();
        User user = userRepository.findByCodLogin(codLogin)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Set<String> roles = userRepository.carregarPerfisEfetivos(user.getIdUsuario())
                .stream()
                .map(Profile::getCodPerfil)
                .collect(Collectors.toSet());

        Set<String> permissions = userRepository.carregarFuncionalidadesEfetivas(user.getIdUsuario())
                .stream()
                .map(Functionality::canonicalKey)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new MeResponse(user.getIdUsuario(), user.getNomUsuario(), roles, permissions));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Trocar senha", description = "Permite ao usuário autenticado trocar sua própria senha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ResponseSimple> changePassword(@RequestBody ResquestChangePassword req) {
        ResetPasswordComand reset = new ResetPasswordComand(req.token(), req.novaSenha());
        casoUso.resetPassword(reset);
        return ResponseEntity.ok(new ResponseSimple(
                "Senha redefinida com sucesso. Faça login com a nova senha."));
    }

    @PostMapping("/recovery-password")
    public ResponseEntity<ResponseSimple> recoveryPassword(@RequestBody RequestRecoveryPassword req) {

        ResetRequestComand cmd = new ResetRequestComand(req.email());
        casoUso.recoveryResetPassword(cmd);
        // Resposta genérica — não confirma se e-mail existe (segurança)
        return ResponseEntity.ok(new ResponseSimple(
                "Se o e-mail estiver cadastrado, você receberá as instruções em instantes."));
    }

    @PostMapping("/login-test")
    public ResponseEntity<String> loginTest(@RequestBody LoginDTO req) {
        return ResponseEntity.ok("Login recebido: " + req.login + ", Password recebido: " + req.password);
    }

    // ── DTOs ─────────────────────────────────────────────────────────────────
    public record RequestLogin(@NotBlank(message = "Login é obrigatório")
            String login,
            @NotBlank(message = "Senha é obrigatória")
            String password) {

    }

    public record ResponseLogin(String token) {

    }

    public record MeResponse(Long id, String name, Set<String> roles, Set<String> permissions) {

    }

    public record ResponseSimple(String mensagem) {

    }

    public record RequestRecoveryPassword(String email) {

    }

    public record ResquestChangePassword(String token, String novaSenha) {

    }

    public static class LoginDTO {
        @NotBlank
        public String login;
        @NotBlank
        public String password;
    }
}
