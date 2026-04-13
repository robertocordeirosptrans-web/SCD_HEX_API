
package br.sptrans.scd.shared.idempotency;

/**
 * Representa o estado do ciclo de vida de uma entrada de idempotência.
 *
 * <ul>
 *   <li>{@code PROCESSING} — requisição em andamento; tentativas concorrentes com a
 *       mesma chave recebem 409 Conflict.</li>
 *   <li>{@code SUCCESS} — processamento concluído; a resposta armazenada é retornada
 *       diretamente em requisições repetidas.</li>
 *   <li>{@code FAILED} — processamento falhou; a mesma chave pode ser reutilizada
 *       para um retry.</li>
 * </ul>
 */
public enum IdempotencyStatus {
    PROCESSING,
    SUCCESS,
    FAILED
}
