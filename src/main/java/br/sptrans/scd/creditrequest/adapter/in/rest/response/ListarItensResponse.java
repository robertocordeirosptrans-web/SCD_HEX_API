package br.sptrans.scd.creditrequest.adapter.in.rest.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListarItensResponse {
    private Long numSolicitacaoItem;
    private String numLogicoCartao;
    private String codProduto;
    private String codSituacao;
    private Integer qtdItem;
    private BigDecimal vlUnitario;
    private BigDecimal vlItem;
    private LocalDateTime dtRecarga;
    private BigDecimal vlCarregado;
    private BigDecimal vlAjuste;
    private BigDecimal vlTxadm;
    private BigDecimal vlTxserv;
    private String flgAjuste;

    public static ListarItensResponse fromDomain(CreditRequestItems item) {
        return ListarItensResponse.builder()
                .numSolicitacaoItem(item.getId() != null ? item.getId().getNumSolicitacaoItem() : null)
                .numLogicoCartao(item.getNumLogicoCartao())
                .codProduto(item.getCodProduto())
                .codSituacao(item.getCodSituacao() != null ? item.getCodSituacao().getCode() : null)
                .qtdItem(item.getQtdItem())
                .vlUnitario(item.getVlUnitario())
                .vlItem(item.getVlItem())
                .dtRecarga(item.getDtRecarga())
                .vlCarregado(item.getVlCarregado())
                .vlAjuste(item.getVlAjuste())
                .vlTxadm(item.getVlTxadm())
                .vlTxserv(item.getVlTxserv())
                .flgAjuste(item.getFlgAjuste())
                .build();
    }
}
