package br.sptrans.scd.shared.idempotency.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.shared.idempotency.entity.IdempotencyLogEntity;
import jakarta.persistence.LockModeType;

/**
 * Repositório Spring Data JPA para {@link IdempotencyLogEntity}.
 */
public interface IdempotencyLogJpaRepository extends JpaRepository<IdempotencyLogEntity, String> {

    /**
     * Busca uma entrada com bloqueio pessimista de escrita, garantindo que nenhuma
     * outra transação possa modificar o registro simultaneamente.
     *
     * <p>Utilizado pelo {@code IdempotencyTxHelper} ao avaliar registros existentes
     * (PROCESSING stale, FAILED para retry) de forma atômica.</p>
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM IdempotencyLogEntity e WHERE e.idempotencyKey = :key")
    Optional<IdempotencyLogEntity> findByKeyForUpdate(@Param("key") String key);

    /**
     * Remove todos os registros com {@code CREATED_AT} anterior ao instante informado.
     * Utilizado pelo job de limpeza por TTL.
     */
    @Modifying
    @Query("DELETE FROM IdempotencyLogEntity e WHERE e.createdAt < :cutoff")
    int deleteByCreatedAtBefore(@Param("cutoff") LocalDateTime cutoff);
}
