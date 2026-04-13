package br.sptrans.scd.shared.idempotency;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação em memória de IdempotencyStore.
 * <p><strong>Não usar em produção.</strong> Apenas para testes ou protótipos.
 * Em produção, utilize {@code DatabaseIdempotencyStore}.</p>
 */
public class InMemoryIdempotencyStore<T> implements IdempotencyStore<T> {

    private final Map<String, T> store = new ConcurrentHashMap<>();
    private final Map<String, IdempotencyStatus> statusMap = new ConcurrentHashMap<>();
    private final Map<String, String> hashMap = new ConcurrentHashMap<>();

    @Override
    public Optional<T> get(String key) {
        if (statusMap.getOrDefault(key, IdempotencyStatus.SUCCESS) == IdempotencyStatus.SUCCESS) {
            return Optional.ofNullable(store.get(key));
        }
        return Optional.empty();
    }

    @Override
    public void put(String key, T response) {
        store.put(key, response);
        statusMap.put(key, IdempotencyStatus.SUCCESS);
    }

    @Override
    public boolean tryMarkProcessing(String key, String payloadHash) {
        IdempotencyStatus existing = statusMap.putIfAbsent(key, IdempotencyStatus.PROCESSING);
        if (existing == null) {
            hashMap.put(key, payloadHash);
            return true;
        }
        if (existing == IdempotencyStatus.FAILED) {
            statusMap.put(key, IdempotencyStatus.PROCESSING);
            hashMap.put(key, payloadHash);
            store.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public void markSuccess(String key, T response) {
        store.put(key, response);
        statusMap.put(key, IdempotencyStatus.SUCCESS);
    }

    @Override
    public void markFailed(String key) {
        statusMap.put(key, IdempotencyStatus.FAILED);
        store.remove(key);
    }

    @Override
    public Optional<IdempotencyStatus> getStatus(String key) {
        return Optional.ofNullable(statusMap.get(key));
    }

    @Override
    public Optional<String> getPayloadHash(String key) {
        return Optional.ofNullable(hashMap.get(key));
    }
}
