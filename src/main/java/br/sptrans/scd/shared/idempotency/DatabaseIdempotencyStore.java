package br.sptrans.scd.shared.idempotency;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.sptrans.scd.shared.idempotency.IdempotencyTxHelper.AcquireResult;
import br.sptrans.scd.shared.idempotency.entity.IdempotencyLogEntity;

/**
 * Implementação de {@link IdempotencyStore} persistida em banco de dados Oracle.
 *
 * <h3>Estratégia de concorrência</h3>
 * <ol>
 *   <li>Tenta INSERT com status {@code PROCESSING} via {@link IdempotencyTxHelper#tryInsertProcessing}
 *       — se bem-sucedido, o chamador tem exclusividade para processar.</li>
 *   <li>Se PK conflict, avalia o registro existente via
 *       {@link IdempotencyTxHelper#acquireExisting} com {@code SELECT FOR UPDATE}.</li>
 * </ol>
 *
 * <h3>Serialização</h3>
 * Respostas são armazenadas como JSON no campo {@code RESPONSE_BODY} (CLOB Oracle).
 * A desserialização utiliza um {@link TypeReference} fornecido na construção do bean.
 *
 * @param <T> tipo da resposta armazenada
 */
public class DatabaseIdempotencyStore<T> implements IdempotencyStore<T> {

    private static final Logger log = LoggerFactory.getLogger(DatabaseIdempotencyStore.class);

    private final IdempotencyTxHelper txHelper;
    private final ObjectMapper objectMapper;
    private final TypeReference<T> typeReference;
    private final int processingTimeoutMinutes;

    public DatabaseIdempotencyStore(
            IdempotencyTxHelper txHelper,
            ObjectMapper objectMapper,
            TypeReference<T> typeReference,
            int processingTimeoutMinutes) {
        this.txHelper = txHelper;
        this.objectMapper = objectMapper;
        this.typeReference = typeReference;
        this.processingTimeoutMinutes = processingTimeoutMinutes;
    }

    @Override
    public boolean tryMarkProcessing(String key, String payloadHash) {
        if (key == null || key.isBlank()) {
            log.debug("[idempotency] Chave ausente — ignorando controle de idempotência");
            return true;
        }
        boolean inserted = txHelper.tryInsertProcessing(key, payloadHash);
        if (inserted) {
            return true;
        }
        AcquireResult result = txHelper.acquireExisting(key, payloadHash, processingTimeoutMinutes);
        log.info("[idempotency] tryMarkProcessing - key={}, acquireResult={}", key, result);
        return result == AcquireResult.ACQUIRED;
    }

    @Override
    public Optional<T> get(String key) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        return txHelper.findRecord(key)
                .filter(e -> e.getStatus() == IdempotencyStatus.SUCCESS)
                .filter(e -> e.getResponseBody() != null)
                .map(e -> deserialize(e.getResponseBody(), key));
    }

    @Override
    public void put(String key, T response) {
        markSuccess(key, response);
    }

    @Override
    public void markSuccess(String key, T response) {
        if (key == null || key.isBlank()) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(response);
            txHelper.markSuccess(key, json, 201);
        } catch (Exception e) {
            log.error("[idempotency] Falha ao serializar resposta para SUCCESS - key={}", key, e);
        }
    }

    @Override
    public void markFailed(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        txHelper.markFailed(key);
    }

    @Override
    public Optional<IdempotencyStatus> getStatus(String key) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        return txHelper.findRecord(key).map(IdempotencyLogEntity::getStatus);
    }

    @Override
    public Optional<String> getPayloadHash(String key) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        return txHelper.findRecord(key).map(IdempotencyLogEntity::getRequestHash);
    }

    private T deserialize(String json, String key) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("[idempotency] Falha ao desserializar resposta - key={}", key, e);
            return null;
        }
    }
}
