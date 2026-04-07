package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record TypesActivityResponseDTO(
        String codAtividade,
        String desAtividade,
        String codStatus,
        LocalDateTime dtCadastro,
        LocalDateTime dtManutencao
) {
}
