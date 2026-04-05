package br.sptrans.scd.shared.idempotency;

import java.util.Optional;

/**
 * Abstração para armazenamento de chaves de idempotência.
 *
 * <p>Permite que a API detecte requisições duplicadas e retorne o resultado
 * anterior sem reprocessar, conforme o padrão:
 * {@code Idempotency-Key: {codCanal}-{numLote}-{dataGeracao}}</p>
 *
 * @param <T> tipo da resposta armazenada
 */
public interface IdempotencyStore<T> {
    /**
     * Retorna a resposta previamente armazenada para a chave informada,
     * ou {@link Optional#empty()} se não existir.
     *
     * @param key chave de idempotência
     * @return resposta armazenada, se houver
     */
    Optional<T> get(String key);

    /**
     * Armazena a resposta associada à chave de idempotência.
     *
     * @param key      chave de idempotência
     * @param response resposta a ser armazenada
     */
    void put(String key, T response);
}
