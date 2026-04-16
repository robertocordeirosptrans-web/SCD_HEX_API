package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import br.sptrans.scd.channel.adapter.in.rest.dto.UserSimpleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareDetailResponseDTO {
    private String codTarifa;
    private String codProduto;
    private String nomProduto;
    private String codVersao;
    private LocalDateTime dtVigenciaIni;
    private LocalDateTime dtVigenciaFim;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private String desTarifa;
    private String stTarifas;
    private Integer vlTarifa;
    private UserSimpleDTO usuarioCadastro;
    private UserSimpleDTO usuarioManutencao;
}
