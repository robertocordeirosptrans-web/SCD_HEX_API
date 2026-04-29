package br.sptrans.scd.auth.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.AuthComand;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.ResetPasswordComand;
import br.sptrans.scd.auth.application.port.in.AuthUseCase.ResetRequestComand;
import br.sptrans.scd.auth.application.usecases.auth.AuthenticateUserUseCase;
import br.sptrans.scd.auth.application.usecases.auth.RecoveryPasswordUseCase;
import br.sptrans.scd.auth.application.usecases.auth.RequestPasswordResetUseCase;
import br.sptrans.scd.auth.domain.User;


/**
 * Testes unitários do AuthService.
 *
 * AuthService é um orquestrador — seus testes verificam apenas que a delegação
 * para os Use Cases ocorre corretamente. Os testes de regras de negócio
 * ficam em AuthenticateUserUseCaseTest, RequestPasswordResetUseCaseTest
 * e ResetPasswordUseCaseTest.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticateUserUseCase authenticateUserUseCase;
    @Mock private RequestPasswordResetUseCase requestPasswordResetUseCase;
    @Mock private RecoveryPasswordUseCase recoveryPasswordUseCase;

    @InjectMocks
    private AuthService authService;

    // ── autenticar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("✓ autenticar() deve delegar para AuthenticateUserUseCase")
    void autenticar_shouldDelegateToUseCase() {
        AuthComand cmd = new AuthComand("admin", "senha123");
        User expectedUser = new User();
        when(authenticateUserUseCase.authenticate(cmd)).thenReturn(expectedUser);

        User result = authService.autenticar(cmd);

        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(authenticateUserUseCase).authenticate(cmd);
    }

    // ── loadUserContext ───────────────────────────────────────────────────────

    @Test
    @DisplayName("✓ loadUserContext() deve delegar para AuthenticateUserUseCase")
    void loadUserContext_shouldDelegateToUseCase() {
        String codLogin = "admin";
        AuthUseCase.UserContext expectedContext = new AuthUseCase.UserContext(
                1L, "Admin", java.util.Set.of(), java.util.Set.of(), java.util.Set.of());
        when(authenticateUserUseCase.loadUserContext(codLogin)).thenReturn(expectedContext);

        AuthUseCase.UserContext result = authService.loadUserContext(codLogin);

        assertNotNull(result);
        assertEquals(expectedContext, result);
        verify(authenticateUserUseCase).loadUserContext(codLogin);
    }

    // ── recoveryResetPassword ─────────────────────────────────────────────────

    @Test
    @DisplayName("✓ recoveryResetPassword() deve delegar para RequestPasswordResetUseCase")
    void recoveryResetPassword_shouldDelegateToUseCase() {
        ResetRequestComand cmd = new ResetRequestComand("user@example.com");

        authService.recoveryResetPassword(cmd);

        verify(requestPasswordResetUseCase).requestPasswordReset(cmd);
    }

    // ── resetPassword ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("✓ resetPassword() deve delegar para RecoveryPasswordUseCase")
    void resetPassword_shouldDelegateToUseCase() {
        ResetPasswordComand cmd = new ResetPasswordComand("token-uuid", "NovaSenha@123");

        authService.resetPassword(cmd);

        verify(recoveryPasswordUseCase).recoveryPassword(cmd);
    }
}