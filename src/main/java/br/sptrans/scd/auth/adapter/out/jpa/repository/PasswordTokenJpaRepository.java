package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.auth.adapter.out.persistence.entity.PasswordTokenEntityJpa;

/**
 * Repositório JPA — Tokens de Redefinição de Senha
 * 
 * Operações: - Buscar token por UUID - Buscar token ativo (não expirado, não usado)
 * - Invalidar tokens de um usuário - Limpar tokens expirados
 */
public interface PasswordTokenJpaRepository extends JpaRepository<PasswordTokenEntityJpa, Long> {

    Optional<PasswordTokenEntityJpa> findByToken(String token);

    Optional<PasswordTokenEntityJpa> findByIdUsuarioAndUsedFalse(Long idUsuario);

    @Modifying
    @Query("UPDATE PasswordTokenEntityJpa t SET t.used = true WHERE t.idUsuario = :idUsuario AND t.used = false")
    void invalidateTokensForUser(@Param("idUsuario") Long idUsuario);

    @Modifying
    @Query("DELETE FROM PasswordTokenEntityJpa t WHERE t.dtExpiracao <= CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
