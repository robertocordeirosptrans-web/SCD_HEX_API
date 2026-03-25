package br.sptrans.scd.shared.idempotency;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação em memória de IdempotencyStore.
 * <p>Não usar em produção, apenas para testes ou protótipos.</p>
 */
public class InMemoryIdempotencyStore<T> implements IdempotencyStore<T> {
    private final Map<String, T> store = new ConcurrentHashMap<>();

    @Override
    public Optional<T> get(String key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public void put(String key, T response) {
        store.putIfAbsent(key, response);
    }
}
