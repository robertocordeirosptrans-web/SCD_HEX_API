package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import br.sptrans.scd.channel.adapter.port.in.rest.dto.UserSimpleDTO;

public record ModalityResponseDTO(
    String codModalidade,
    String desModalidade,
    String stModalidade,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    UserSimpleDTO usuarioCadastro,
    UserSimpleDTO usuarioManutencao
) {}
