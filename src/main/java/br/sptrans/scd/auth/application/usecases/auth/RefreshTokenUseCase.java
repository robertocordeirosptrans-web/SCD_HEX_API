package br.sptrans.scd.auth.application.usecases.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.in.SessionManagementUseCase;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.port.out.TokenGeneratorPort;
import br.sptrans.scd.auth.domain.port.out.TokenValidatorPort;
import br.sptrans.scd.auth.domain.session.UserSession;
import br.sptrans.scd.shared.exception.AccountBlockedException;
import br.sptrans.scd.shared.exception.InactiveUserException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Renovar Tokens (Refresh Token)
 *
 * Responsável por:
 * - Validar o refresh token (assinatura, expiração e tipo)
 * - Verificar se o usuário ainda está ativo e não bloqueado
 * - Criar nova sessão rastreada
 * - Gerar novo par de tokens (access + refresh) com rotação de refresh token
 */
@Component
@Transactional
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenUseCase.class);

    private final TokenValidatorPort tokenValidator;
    private final TokenGeneratorPort tokenGenerator;
    private final UserQueryPort userQueryPort;
    private final SessionManagementUseCase sessionManagementUseCase;

    /**
     * Valida o refresh token, verifica o usuário e retorna novo par de tokens.
     * O refresh token anterior é implicitamente invalidado pela rotação.
     *
     * @param comand contém o refresh token e dados de contexto HTTP
     * @return novo par {accessToken, refreshToken}
     */
    public AuthUseCase.TokenPair refresh(AuthUseCase.RefreshTokenComand comand) {
        log.info("Iniciando renovação de tokens via refresh token");

        // Valida o refresh token — lança ExpiredTokenException / InvalidTokenException se inválido
        String codLogin = tokenValidator.validateRefreshToken(comand.refreshToken());

        // Carrega usuário
        User user = userQueryPort.findByCodLogin(codLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "login", codLogin));

        // Verifica status do usuário
        if (user.isBlocked()) {
            log.warn("Refresh token rejeitado — conta bloqueada. login={}", codLogin);
            throw new AccountBlockedException(
                    "Conta bloqueada. Contate o administrador.");
        }
        if (user.isInactive()) {
            log.warn("Refresh token rejeitado — conta inativa. login={}", codLogin);
            throw new InactiveUserException(
                    "Conta inativa. Contate o administrador.");
        }

        // Cria nova sessão rastreada
        UserSession session = sessionManagementUseCase.createSession(
                user.getIdUsuario(), comand.ip(), comand.userAgent());

        // Gera novo par de tokens (rotação de refresh token)
        String newAccessToken  = tokenGenerator.generate(user, session.getIdSessao());
        String newRefreshToken = tokenGenerator.generateRefresh(user);

        log.info("Tokens renovados com sucesso. login={}, novaSessionId={}", codLogin, session.getIdSessao());
        return new AuthUseCase.TokenPair(newAccessToken, newRefreshToken);
    }
}
