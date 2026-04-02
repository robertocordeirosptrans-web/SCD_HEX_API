package br.sptrans.scd.auth.application.service;

import org.springframework.stereotype.Service;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.usecases.auth.AuthenticateUserUseCase;
import br.sptrans.scd.auth.application.usecases.auth.RequestPasswordResetUseCase;
import br.sptrans.scd.auth.application.usecases.auth.ResetPasswordUseCase;
import br.sptrans.scd.auth.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;


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
        resetPasswordUseCase.resetPassword(comando);
    }
}



