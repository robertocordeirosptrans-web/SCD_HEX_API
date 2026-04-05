package br.sptrans.scd.product.application.port.out.gateway;

/**
 * Output Port — Gateway para verificação de liminares judiciais de isenção de taxa
 * no sistema SCA via DBLink.
 *
 * <p>A liminar aparece em dois momentos distintos no sistema:</p>
 * <ul>
 *   <li><b>Momento 1</b> — Na validação do pedido (PRC_MVE_PEDIDO_CREDITO_2):
 *       verifica isenção de taxa para a empresa, restrito ao canal 152.</li>
 *   <li><b>Momento 2</b> — No envio ao HM (PCK_MVE_SITUACAOPEDIDO):
 *       verifica liminar para empresa e para o cartão individualmente.</li>
 * </ul>
 */
public interface LiminarGateway {

    /**
     * Momento 1: verifica isenção de taxa no processamento do pedido.
     *
     * <p>Consulta se a empresa associada ao pedido possui liminar judicial de isenção
     * de taxa. Em caso de falha na consulta, retorna {@code false} (cobra taxa normalmente),
     * replicando o comportamento do {@code EXCEPTION WHEN OTHERS THEN NULL} original.</p>
     *
     * @param numeroPedido número da solicitação do pedido
     * @return {@code true} se a empresa possui isenção de taxa; {@code false} caso contrário
     *         ou em caso de falha na consulta
     */
    boolean empresaPossuiIsencaoTaxa(String numeroPedido);

    /**
     * Momento 2 — passo 1: verifica liminar da empresa para envio ao HM.
     *
     * @param numeroPedido número da solicitação do pedido
     * @return 1 se a empresa possui liminar; 0 caso contrário
     */
    int verificarLiminarEmpresa(String numeroPedido);

    /**
     * Momento 2 — passo 2: verifica liminar do cartão para envio ao HM.
     *
     * @param liminarEmpresa resultado de {@link #verificarLiminarEmpresa(String)}
     * @param numeroCartao   número lógico do cartão
     * @return 1 se o cartão (e a empresa) possuem liminar; 0 caso contrário
     */
    int verificarLiminarCartao(int liminarEmpresa, String numeroCartao);

    /**
     * Verificação combinada: consulta se o canal e cartão possuem liminar judicial ativa.
     *
     * <p>Usado no processamento de recarga (canal 152) para determinar isenção de taxas.
     * Em caso de falha, retorna {@code false} (cobra taxa normalmente).</p>
     *
     * @param codCanal        código do canal de distribuição
     * @param numLogicoCartao número lógico do cartão
     * @return {@code true} se existe liminar ativa; {@code false} caso contrário ou em falha
     */
    boolean existeLiminar(String codCanal, String numLogicoCartao);
}
