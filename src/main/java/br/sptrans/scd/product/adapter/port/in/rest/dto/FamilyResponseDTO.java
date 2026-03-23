package br.sptrans.scd.product.adapter.port.in.rest.dto;

import java.time.LocalDateTime;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.UserSimpleDTO;

public record FamilyResponseDTO(
    String codFamilia,
    String desFamilia,
    String stFamilias,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    UserSimpleDTO usuarioCadastro,
    UserSimpleDTO usuarioManutencao
) {}
