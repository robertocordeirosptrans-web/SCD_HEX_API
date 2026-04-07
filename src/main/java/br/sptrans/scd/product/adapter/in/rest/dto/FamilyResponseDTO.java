package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import br.sptrans.scd.channel.adapter.in.rest.dto.UserSimpleDTO;

public record FamilyResponseDTO(
    String codFamilia,
    String desFamilia,
    String codStatus,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    UserSimpleDTO usuarioCadastro,
    UserSimpleDTO usuarioManutencao
) {}
