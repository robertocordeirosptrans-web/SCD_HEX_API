package br.sptrans.scd.product.adapter.in.rest.dto;

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
public class FeeResponseDTO {
    private Long codTaxa;
    private String codProduto;
    private String codCanal;
    private String desTaxa;
    private LocalDateTime dtInicio;
    private LocalDateTime dtFim;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private String codStatus;
    private BigDecimal valFixoAdministrativa;
    private BigDecimal valPercentualAdministrativa;
    private BigDecimal valFixoServico;
    private BigDecimal valPercentualServico;
    private BigDecimal valMinimoServico;
    private String codCanalDestino;
    private Long idUsuarioCadastro;
    private Long idUsuarioManutencao;
}
