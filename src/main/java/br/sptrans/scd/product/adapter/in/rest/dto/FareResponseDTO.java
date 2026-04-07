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
public class FareResponseDTO {
    private String codTarifa;
    private String codProduto;
    private String codVersao;
    private String codCanal;
    private String desTarifa;
    private BigDecimal valTarifa;
    private LocalDateTime dtVigenciaInicio;
    private LocalDateTime dtVigenciaFim;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private String codStatus;
    private Long idUsuarioCadastro;
    private Long idUsuarioManutencao;
}
