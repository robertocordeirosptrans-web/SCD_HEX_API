package br.sptrans.scd.channel.application.port.out.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProdutoCodigoDescricaoDTO {
    private String codigo;
    private String descricao;
}