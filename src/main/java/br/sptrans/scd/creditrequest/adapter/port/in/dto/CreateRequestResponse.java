package br.sptrans.scd.creditrequest.adapter.port.in.dto;

import java.util.List;

/**
 * DTO de resposta para a criação de pedido de crédito em lote.
 *
 * <p>Informa a quantidade de itens processados com sucesso e os rejeitados,
 * incluindo o motivo de rejeição de cada um.</p>
 */

public record CreateRequestResponse(
        int totalItens,
        int itensProcessados,
        int itensRejeitados,
        List<ItemProcessado> pedidosProcessados,
        List<ItemRejeitado> pedidosRejeitados
        ) {

    /**
     * Representa um item aceito e persistido com sucesso.
     */
    public record ItemProcessado(
            Long numSolicitacao,
            String numLogicoCartao,
            String codProduto,
            String codSituacao
            ) {

    }

    /**
     * Representa um item rejeitado na validação, com o motivo da rejeição.
     */
    public record ItemRejeitado(
            Long numSolicitacao,
            String numLogicoCartao,
            String codProduto,
            String motivoRejeicao
            ) {

    }
}
