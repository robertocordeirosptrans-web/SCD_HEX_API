package br.sptrans.scd.product.adapter.port.in.rest.dto;

import java.time.LocalDateTime;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.UserSimpleDTO;

public record ProductsTypeResponseDTO(
    String codTipoProduto,
    String desTipoProduto,
    String stTipoProduto,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    UserSimpleDTO usuarioCadastro,
    UserSimpleDTO usuarioManutencao
) {}
