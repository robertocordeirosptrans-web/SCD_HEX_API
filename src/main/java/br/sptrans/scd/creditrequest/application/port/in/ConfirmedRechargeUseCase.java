package br.sptrans.scd.creditrequest.application.port.in;

/**
 * Porta de entrada (use case) para confirmar a recarga de um pedido,
 * consultando o retorno do Hardware Manager e atualizando os itens confirmados
 * para {@code RECARREGADO (07)}.
 *
 * <p>
 * Equivalente à rotina {@code ConfirmarRecarga} da package Oracle
 * {@code PCK_MVE_SITUACAOPEDIDO}.</p>
 */
public interface ConfirmedRechargeUseCase {

    /**
     * Verifica e confirma os itens em {@code EM_PROCESSO_DE_RECARGA (06)} de um
     * pedido que foram processados pelo HM.
     *
     * <p>
     * Para cada item confirmado (NI_STATUSHM = 3, NI_STATUSSCD = 15):</p>
     * <ul>
     * <li>Atualiza o item para {@code RECARREGADO (07)} com
     * {@code dtRetornoHM}.</li>
     * </ul>
     * <p>
     * Ao final, recalcula a situação consolidada do pedido.</p>
     *
     * @param numSolicitacao número da solicitação
     * @param codCanal código do canal
     */
    void confirmarRecarga(Long numSolicitacao, String codCanal);
}
