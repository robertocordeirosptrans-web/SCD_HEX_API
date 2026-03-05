package br.sptrans.scd.auth.adapter.in.rest;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.AuthComand;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.ResetPasswordComand;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.ResetRequestComand;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/auth")
@Tag(name = "Autenticação v1", description = "Endpoints para autenticação e gerenciamento de usuários - Versão 1")
public class AuthController {

    private final AuthUseCase casoUso;
    private final ProviderJwtToken providerJwtToken;

    public AuthController(AuthUseCase casoUso, ProviderJwtToken providerJwtToken) {
        this.casoUso = casoUso;
        this.providerJwtToken = providerJwtToken;

    }

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Autentica o usuário e retorna um token JWT com payload mínimo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Credenciais inválidas ou usuário bloqueado/inativo")
    })
    public ResponseEntity<ResponseLogin> login(@RequestBody RequestLogin req) {
        AuthComand auth = new AuthComand(req.codLogin(), req.senha());
        User user = casoUso.autenticar(auth);
        Set<String> permissoes = user.getFuncionalidadesUsuario().stream()
                .map(Functionality::canonicalKey)
                .collect(Collectors.toSet());
        String jwt = providerJwtToken.gerarToken(user.getIdUsuario(), user.getCodLogin(), permissoes);

        ResponseLogin res = new ResponseLogin(jwt,
                user.getIdUsuario(),
                user.getNomUsuario(),
                permissoes);

        return ResponseEntity.ok(res);
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



    // ── DTOs ─────────────────────────────────────────────────────────────────
    record RequestLogin(String codLogin, String senha) { }
    record ResponseLogin(String token, Long idUsuario, String nomUsuario, Set<String> permissoes) {}
    record ResponseSimple(String mensagem) {}
    record RequestRecoveryPassword(String email) {}
    record ResquestChangePassword(String token, String novaSenha) {}
}
