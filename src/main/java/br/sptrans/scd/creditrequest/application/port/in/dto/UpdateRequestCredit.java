package br.sptrans.scd.creditrequest.application.port.in.dto;

import java.util.List;

import br.sptrans.scd.creditrequest.domain.enums.ActionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestCredit {

    @NotNull(message = "A ação é obrigatória")
    private ActionStatus acao;

    @NotNull(message = "O código do canal é obrigatório")
    private String codCanal;

    @Valid
    @NotNull(message = "A lista de itens permitidos é obrigatória")
    private List<AllowedItem> itensPermitidos;

    private String observacao;

    // Campos específicos para ação PAGO
    private String codFormaPagto;  // Obrigatório apenas para PAGO
    private Double vlPago;  // Opcional - se informado, será validado

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllowedItem {

        @NotNull(message = "O número da solicitação é obrigatório")
        private Long numSolicitacao;

        private Long numLote;

        @Valid
        private ItemDetail item;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDetail {

        private String codProduto;
        private String codSituacao;
        private String dtRecarga;
        private String flgAjuste;
        private Long numLogicoCartao;
        private Long numSolicitacaoItem;
        private Integer qtdItem;
        private Double vlAjuste;
        private Double vlCarregado;
        private Double vlItem;
        private Double vlTxadm;
        private Double vlTxserv;
        private Double vlUnitario;
    }

}
