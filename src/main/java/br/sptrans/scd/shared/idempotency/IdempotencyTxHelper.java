package br.sptrans.scd.shared.idempotency;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.shared.idempotency.entity.IdempotencyLogEntity;
import br.sptrans.scd.shared.idempotency.repository.IdempotencyLogJpaRepository;
import lombok.RequiredArgsConstructor;

/**
 * Componente auxiliar que encapsula operações transacionais atômicas de idempotência.
 *
 * <p>Cada método utiliza {@code REQUIRES_NEW} para garantir que a operação seja
 * confirmada ou revertida independentemente da transação chamadora. Isso é necessário
 * porque:
 * <ul>
 *   <li>{@link #tryInsertProcessing} pode lançar {@link DataIntegrityViolationException}
 *       — capturada e tratada sem contaminar sessões Hibernate externas.</li>
 *   <li>{@link #markFailed} é chamado em blocos {@code catch}, onde a transação
 *       principal pode estar marcada para rollback.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class IdempotencyTxHelper {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyTxHelper.class);

    private final IdempotencyLogJpaRepository repository;

    /**
     * Resultado possível ao tentar adquirir uma chave existente.
     */
    public enum AcquireResult {
        /** Chave adquirida: pode prosseguir com o processamento. */
        ACQUIRED,
        /** Registro existente com status SUCCESS: retornar resposta cacheada. */
        SUCCESS_EXISTS,
        /** Registro existente em PROCESSING e não expirado: retornar 409. */
        STILL_PROCESSING,
        /** Estado inconsistente: tratar como conflito. */
        CONFLICT
    }

    /**
     * Tenta inserir um novo registro com status {@code PROCESSING}.
     *
     * @return {@code true} se o INSERT foi bem-sucedido; {@code false} se a chave já existe (PK)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW,
                   noRollbackFor = DataIntegrityViolationException.class)
    public boolean tryInsertProcessing(String key, String payloadHash) {
        try {
            IdempotencyLogEntity entity = new IdempotencyLogEntity();
            LocalDateTime now = LocalDateTime.now();
            entity.setIdempotencyKey(key);
            entity.setRequestHash(payloadHash);
            entity.setStatus(IdempotencyStatus.PROCESSING);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            repository.saveAndFlush(entity);
            log.info("[idempotency] Chave registrada como PROCESSING - key={}", key);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.debug("[idempotency] Chave já existe (PK conflict) - key={}", key);
            return false;
        }
    }

    /**
     * Avalia e tenta adquirir uma chave já existente.
     *
     * <p>Utiliza {@code SELECT FOR UPDATE} para garantir exclusão mútua:</p>
     * <ul>
     *   <li>{@code SUCCESS} → retorna {@link AcquireResult#SUCCESS_EXISTS}</li>
     *   <li>{@code PROCESSING} não expirado → retorna {@link AcquireResult#STILL_PROCESSING}</li>
     *   <li>{@code PROCESSING} expirado → reseta para PROCESSING, retorna {@link AcquireResult#ACQUIRED}</li>
     *   <li>{@code FAILED} → reseta para PROCESSING (permite retry), retorna {@link AcquireResult#ACQUIRED}</li>
     * </ul>
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcquireResult acquireExisting(String key, String payloadHash, int processingTimeoutMinutes) {
        Optional<IdempotencyLogEntity> opt = repository.findByKeyForUpdate(key);
        if (opt.isEmpty()) {
            log.warn("[idempotency] Registro não encontrado após PK conflict - key={}", key);
            return AcquireResult.CONFLICT;
        }

        IdempotencyLogEntity entity = opt.get();
        LocalDateTime now = LocalDateTime.now();

        switch (entity.getStatus()) {

            case SUCCESS:
                log.info("[idempotency] Replay: registro SUCCESS encontrado - key={}", key);
                return AcquireResult.SUCCESS_EXISTS;

            case PROCESSING:
                boolean stale = entity.getUpdatedAt()
                        .plusMinutes(processingTimeoutMinutes)
                        .isBefore(now);
                if (stale) {
                    log.warn("[idempotency] PROCESSING expirado detectado — resetando - key={}, updatedAt={}",
                            key, entity.getUpdatedAt());
                    entity.setRequestHash(payloadHash);
                    entity.setUpdatedAt(now);
                    repository.save(entity);
                    return AcquireResult.ACQUIRED;
                }
                log.info("[idempotency] Requisição em PROCESSING ativo - key={}", key);
                return AcquireResult.STILL_PROCESSING;

            case FAILED:
                log.info("[idempotency] Retry permitido para registro FAILED - key={}", key);
                entity.setStatus(IdempotencyStatus.PROCESSING);
                entity.setRequestHash(payloadHash);
                entity.setResponseBody(null);
                entity.setHttpStatus(null);
                entity.setUpdatedAt(now);
                repository.save(entity);
                return AcquireResult.ACQUIRED;

            default:
                return AcquireResult.CONFLICT;
        }
    }

    /**
     * Atualiza o registro para {@code SUCCESS} e persiste a resposta serializada.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(String key, String responseJson, int httpStatus) {
        repository.findById(key).ifPresent(entity -> {
            entity.setStatus(IdempotencyStatus.SUCCESS);
            entity.setResponseBody(responseJson);
            entity.setHttpStatus(httpStatus);
            entity.setUpdatedAt(LocalDateTime.now());
            repository.save(entity);
            log.info("[idempotency] Registro atualizado para SUCCESS - key={}", key);
        });
    }

    /**
     * Atualiza o registro para {@code FAILED}.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String key) {
        repository.findById(key).ifPresent(entity -> {
            entity.setStatus(IdempotencyStatus.FAILED);
            entity.setUpdatedAt(LocalDateTime.now());
            repository.save(entity);
            log.warn("[idempotency] Registro atualizado para FAILED - key={}", key);
        });
    }

    /**
     * Retorna o registro de idempotência para a chave especificada.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<IdempotencyLogEntity> findRecord(String key) {
        return repository.findById(key);
    }

    /**
     * Remove registros criados antes do instante informado (TTL cleanup).
     *
     * @return número de registros removidos
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteExpiredRecords(LocalDateTime cutoff) {
        int deleted = repository.deleteByCreatedAtBefore(cutoff);
        log.info("[idempotency] Limpeza de registros expirados: {} removidos (cutoff={})", deleted, cutoff);
        return deleted;
    }
}
