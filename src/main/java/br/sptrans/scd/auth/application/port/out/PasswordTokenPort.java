package br.sptrans.scd.auth.application.port.out;

import java.util.Optional;

import br.sptrans.scd.auth.domain.PasswordResetToken;

/**
 * Porta de Saída — repositório de tokens de redefinição de senha. Tabela:
 * password_reset_tokens.
 */
public interface PasswordTokenPort {

    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Retorna o token vigente (não expirado, não usado) do usuário, se existir.
     */
    Optional<PasswordResetToken> findByIdUsuarioAndUsedFalse(Long idUsuario);

    void save(PasswordResetToken token);

    void invalidateTokensForUser(Long idUsuario);

    void deleteExpiredTokens();

}
