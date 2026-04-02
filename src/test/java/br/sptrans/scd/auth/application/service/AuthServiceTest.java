package br.sptrans.scd.auth.application.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.sptrans.scd.auth.application.port.in.AuthUseCase.AuthComand;
import br.sptrans.scd.auth.application.port.in.PasswordValidator;
import br.sptrans.scd.auth.application.port.out.AuthenticationRepository;
import br.sptrans.scd.auth.application.port.out.AuthorizationRepository;
import br.sptrans.scd.auth.application.port.out.GatewayEmail;
import br.sptrans.scd.auth.application.port.out.GroupUserRepository;
import br.sptrans.scd.auth.application.port.out.PasswordTokenRepository;
import br.sptrans.scd.auth.application.port.out.UserReader;
import br.sptrans.scd.auth.application.port.out.UserStatusRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.auth.domain.vo.AccessPolicy;
import br.sptrans.scd.auth.domain.vo.Credentials;
import br.sptrans.scd.auth.domain.vo.PersonalInfo;
import br.sptrans.scd.auth.domain.vo.UserAudit;
import br.sptrans.scd.shared.exception.AccountBlockedException;
import br.sptrans.scd.shared.exception.AuthenticationFailedException;
import br.sptrans.scd.shared.security.PasswordHashUtil;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock private UserReader userReader;
    @Mock private UserStatusRepository userStatusRepository;
    @Mock private AuthenticationRepository authenticationRepository;
    @Mock private AuthorizationRepository authorizationRepository;
    @Mock private GroupUserRepository groupUserRepository;
    @Mock private PasswordTokenRepository tokenRepository;
    @Mock private GatewayEmail gatewayEmail;
    @Mock private PasswordValidator passwordValidator;

    @InjectMocks
    private AuthService authService;

    private static final String LOGIN = "admin";
    private static final String PASSWORD = "password123";

    private String hashedPassword;

    @BeforeEach
    void setUp() {
        hashedPassword = PasswordHashUtil.hashBcrypt(PASSWORD);
    }

    private User buildActiveUser() {
        User user = new User();
        user.setIdUsuario(1L);
        user.setCredentials(Credentials.builder()
                .codLogin(LOGIN)
                .codSenha(hashedPassword)
                .numTentativasFalha(0)
                .build());
        user.setPersonalInfo(PersonalInfo.builder()
                .nomUsuario("Admin User")
                .nomEmail("admin@example.com")
                .build());
        user.setAudit(UserAudit.builder()
                .codStatus(UserStatus.ACTIVE)
                .dtCriacao(LocalDateTime.now())
                .idUsuarioManutencao(1L)
                .build());
        user.setAccessPolicy(AccessPolicy.semRestricao());
        return user;
    }

    @Test
    @DisplayName("✓ Deve autenticar usuário com credenciais válidas")
    void shouldAuthenticateWithValidCredentials() {
        User user = buildActiveUser();

        when(userReader.findByCodLogin(LOGIN))
                .thenReturn(Optional.of(user));

        User result = authService.autenticar(new AuthComand(LOGIN, PASSWORD));

        assertNotNull(result);
        assertEquals(LOGIN, result.getCodLogin());
        verify(authenticationRepository).atualizarUltimoAcesso(user.getIdUsuario());
    }

    @Test
    @DisplayName("✗ Deve lançar exceção para usuário não encontrado")
    void shouldThrowExceptionForUnknownUser() {
        when(userReader.findByCodLogin("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class,
                () -> authService.autenticar(new AuthComand("unknown", PASSWORD)));
    }

    @Test
    @DisplayName("✗ Deve bloquear conta após 3 tentativas falhas")
    void shouldBlockAccountAfterThreeFailedAttempts() {
        User user = new User();
        user.setIdUsuario(1L);
        user.setCredentials(Credentials.builder()
                .codLogin(LOGIN)
                .codSenha(PasswordHashUtil.hashBcrypt("correctPassword"))
                .numTentativasFalha(0)
                .build());
        user.setAudit(UserAudit.builder()
                .codStatus(UserStatus.ACTIVE)
                .dtCriacao(LocalDateTime.now())
                .idUsuarioManutencao(1L)
                .build());
        user.setAccessPolicy(AccessPolicy.semRestricao());

        when(userReader.findByCodLogin(LOGIN))
                .thenReturn(Optional.of(user));

        // Primeiras duas tentativas lançam AuthenticationFailedException
        assertThrows(AuthenticationFailedException.class,
                () -> authService.autenticar(new AuthComand(LOGIN, PASSWORD)));
        assertThrows(AuthenticationFailedException.class,
                () -> authService.autenticar(new AuthComand(LOGIN, PASSWORD)));

        // Terceira tentativa deve bloquear a conta
        assertThrows(AccountBlockedException.class,
                () -> authService.autenticar(new AuthComand(LOGIN, PASSWORD)));
    }
}
