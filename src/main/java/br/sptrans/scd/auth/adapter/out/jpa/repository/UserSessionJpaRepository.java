package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserSessionEntityJpa;

public interface UserSessionJpaRepository extends JpaRepository<UserSessionEntityJpa, String> {

    /**
     * Sessões ativas = sem DT_REVOGACAO e com DT_EXPIRACAO no futuro.
     */
    List<UserSessionEntityJpa> findByIdUsuarioAndDtRevogacaoIsNullAndDtExpiracaoAfter(
            Long idUsuario, LocalDateTime now);

    /**
     * Revoga todas as sessões ativas de um usuário (logout total / troca de senha).
     */
    @Modifying
    @Query("UPDATE UserSessionEntityJpa s " +
           "SET s.dtRevogacao = :dtRevogacao, s.motivoRevogacao = :motivo " +
           "WHERE s.idUsuario = :userId AND s.dtRevogacao IS NULL")
    int revokeAllActiveByUserId(@Param("userId") Long userId,
                                @Param("dtRevogacao") LocalDateTime dtRevogacao,
                                @Param("motivo") String motivo);

    /**
     * Job de expiração: marca todas as sessões cujo prazo já passou e
     * que ainda não foram revogadas explicitamente.
     */
    @Modifying
    @Query("UPDATE UserSessionEntityJpa s " +
           "SET s.dtRevogacao = :agora, s.motivoRevogacao = 'EXPIRED' " +
           "WHERE s.dtRevogacao IS NULL AND s.dtExpiracao < :agora")
    int expireSessions(@Param("agora") LocalDateTime agora);
}
