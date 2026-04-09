package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.math.BigDecimal;

/**
 * Porta de saída para cálculo de eventos financeiros via Oracle Package.
 *
 * <p>Encapsula a chamada à função {@code PCK_MVE_EVENTO_FINANCEIRO.ProcessarLancamento}
 * que calcula e persiste o lançamento financeiro vinculado a um item de recarga.</p>
 */
public interface EventoFinanceiroPort {

    /**
     * Processa o lançamento financeiro de um item e retorna o valor do evento calculado.
     * Equivalente a {@code PCK_MVE_EVENTO_FINANCEIRO.ProcessarLancamento(pedido, canal, item)}.
     *
     * @param numSolicitacao   número da solicitação
     * @param codCanal         código do canal
     * @param numSolicitacaoItem número do item da solicitação
     * @return valor do evento financeiro (pode ser negativo ou zero)
     */
    BigDecimal processarLancamento(Long numSolicitacao, String codCanal, Long numSolicitacaoItem);
}
