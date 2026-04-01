package br.sptrans.scd.auth.application.port.out;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.sptrans.scd.auth.adapter.port.out.persistence.entity.PasswordTokenEntityJpa;
import br.sptrans.scd.auth.domain.PasswordResetToken;

/**
 * Porta de Saída — repositório de tokens de redefinição de senha. Tabela:
 * password_reset_tokens.
 */
public interface PasswordTokenRepository extends JpaRepository<PasswordTokenEntityJpa, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Retorna o token vigente (não expirado, não usado) do usuário, se existir.
     */
    Optional<PasswordResetToken> findByIdUsuarioAndUsedFalse(Long idUsuario);

    void save(PasswordResetToken token);

    @Modifying
    @Query("UPDATE PasswordTokenEntityJpa t SET t.used = true WHERE t.idUsuario = :idUsuario AND t.used = false")
    void invalidateTokensForUser(Long idUsuario);

    @Modifying
    @Query("DELETE FROM PasswordTokenEntityJpa t WHERE t.expiryDate <= CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

}
