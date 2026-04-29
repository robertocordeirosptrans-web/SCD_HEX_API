package br.sptrans.scd.auth.application.service;

import org.springframework.stereotype.Service;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.usecases.auth.AuthenticateUserUseCase;
import br.sptrans.scd.auth.application.usecases.auth.ChangePasswordUseCase;
import br.sptrans.scd.auth.application.usecases.auth.RecoveryPasswordUseCase;
import br.sptrans.scd.auth.application.usecases.auth.RefreshTokenUseCase;
import br.sptrans.scd.auth.application.usecases.auth.RequestPasswordResetUseCase;
import br.sptrans.scd.auth.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final RecoveryPasswordUseCase recoveryPasswordUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;


    // ── Autenticando um usuario ───────────────────────────────────────────────────────────
    @Override
    public User autenticar(AuthComand comando) {
        return authenticateUserUseCase.authenticate(comando);
    }

    // ── Recuperação de Senha ───────────────────────────────────────────────────────────
    @Override
    public void recoveryResetPassword(ResetRequestComand comando) {
        requestPasswordResetUseCase.requestPasswordReset(comando);
    }

    @Override
    public AuthUseCase.UserContext loadUserContext(String codLogin) {
        return authenticateUserUseCase.loadUserContext(codLogin);
    }

    // ── redefinirSenha ───────────────────────────────────────────────────────
    @Override
    public void resetPassword(ResetPasswordComand comando) {
        recoveryPasswordUseCase.recoveryPassword(comando);
    }

    // ── Trocar Senha (usuário autenticado) ────────────────────────────────────
    @Override
    public void changePassword(ChangePasswordCommand comando) {
        changePasswordUseCase.changePassword(comando.idUsuario(), comando.senhaAtual(), comando.novaSenha());
    }

    // ── Invalidar cache de permissões ────────────────────────────────────────
    @Override
    public void evictUserPermissionsCache(Long idUsuario) {
        authenticateUserUseCase.evictUserPermissionsCache(idUsuario);
    }

    // ── Renovar tokens (refresh token) ───────────────────────────────────────
    @Override
    public TokenPair refreshToken(RefreshTokenComand comand) {
        return refreshTokenUseCase.refresh(comand);
    }
}



