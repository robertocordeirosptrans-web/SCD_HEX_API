package br.sptrans.scd.shared.idempotency;

import java.util.Optional;

/**
 * Abstração para armazenamento de chaves de idempotência.
 *
 * <p>Suporta ciclo de vida completo via banco de dados:
 * {@code PROCESSING → SUCCESS | FAILED}</p>
 *
 * <p>Fluxo recomendado no caso de uso:</p>
 * <pre>
 *   1. tryMarkProcessing(key, hash) → true = pode prosseguir
 *   2a. Em caso de false: verificar getStatus → SUCCESS → get(key)
 *   2b. Em caso de false: verificar getStatus → PROCESSING → lançar 409
 *   3. Ao final com sucesso: markSuccess(key, response)
 *   4. Em caso de exceção:   markFailed(key)
 * </pre>
 *
 * @param <T> tipo da resposta armazenada
 */
public interface IdempotencyStore<T> {

    /**
     * Retorna a resposta previamente armazenada para a chave informada
     * (apenas registros com status {@code SUCCESS}).
     *
     * @param key chave de idempotência
     * @return resposta armazenada, se houver
     */
    Optional<T> get(String key);

    /**
     * Marca a chave como {@code SUCCESS} e armazena a resposta.
     * Atalho compatível com implementações legadas.
     *
     * @param key      chave de idempotência
     * @param response resposta a ser armazenada
     */
    void put(String key, T response);

    /**
     * Tenta registrar a chave como {@code PROCESSING}.
     *
     * <p>Retorna {@code true} se a chave foi adquirida com sucesso e o chamador
     * pode prosseguir com o processamento. Retorna {@code false} se já existe um
     * registro conflitante — o chamador deve verificar {@link #getStatus} para
     * decidir a resposta adequada.</p>
     *
     * @param key         chave de idempotência
     * @param payloadHash SHA-256 hex do payload da requisição
     * @return {@code true} se adquirida; {@code false} em caso de conflito
     */
    boolean tryMarkProcessing(String key, String payloadHash);

    /**
     * Finaliza o processamento marcando o registro como {@code SUCCESS}.
     *
     * @param key      chave de idempotência
     * @param response resposta a ser persistida
     */
    void markSuccess(String key, T response);

    /**
     * Finaliza o processamento marcando o registro como {@code FAILED}.
     * Registros FAILED permitem retry com a mesma chave.
     *
     * @param key chave de idempotência
     */
    void markFailed(String key);

    /**
     * Retorna o status atual do registro associado à chave.
     *
     * @param key chave de idempotência
     * @return status, ou {@link Optional#empty()} se não existir
     */
    Optional<IdempotencyStatus> getStatus(String key);

    /**
     * Retorna o hash do payload armazenado para a chave.
     *
     * @param key chave de idempotência
     * @return hash SHA-256, ou {@link Optional#empty()} se não existir
     */
    Optional<String> getPayloadHash(String key);
}
