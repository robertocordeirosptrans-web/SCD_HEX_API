package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;

import br.sptrans.scd.creditrequest.domain.CreditRequestRD;

/**
 * Porta de saída para persistência dos canais de distribuição vinculados a um pedido (SOL_RD_DISTRIBUICOES).
 *
 * <p>Utilizada em dois momentos do ciclo do pedido:</p>
 * <ol>
 *   <li><b>Criação:</b> grava a lista definitiva de terminais reais, expandindo supercanais nos seus filhos ativos.</li>
 *   <li><b>Recarga:</b> consultada para recuperar todos os terminais que devem ser registrados no HM.</li>
 * </ol>
 */
public interface CreditRequestRDPort {

    /**
     * Salva em lote os registros de canais de distribuição de um pedido.
     *
     * @param distribuicoes lista de canais de distribuição a persistir; não deve ser nula
     */
    void saveAll(List<CreditRequestRD> distribuicoes);

    /**
     * Retorna todos os canais de distribuição registrados para um pedido.
     *
     * @param numSolicitacao número da solicitação
     * @param codCanal       código do canal comercializador
     * @return lista (possivelmente vazia) de registros de distribuição
     */
    List<CreditRequestRD> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal);
}
