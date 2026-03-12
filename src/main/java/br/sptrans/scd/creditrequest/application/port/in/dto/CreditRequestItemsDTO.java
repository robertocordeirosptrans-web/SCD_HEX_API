package br.sptrans.scd.creditrequest.application.port.in.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestItemsDTO {
    private Long numSolicitacaoItem;
    private String codCanal;
    private String codProduto;
    private String codSituacao;
    private BigDecimal vlItem;
    private LocalDateTime dtRecarga;
    private BigDecimal vlCarregado;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;

    // Novos campos
    private BigDecimal vlTxadm;
    private BigDecimal vlTxserv;
    private BigDecimal vlTxtotal;
}
