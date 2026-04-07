package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record AgreementValidityResponseDTO(
        String codCanal,
        String codProduto,
        LocalDateTime dtInicioValidade,
        LocalDateTime dtFimValidade,
        String codStatus,
        LocalDateTime dtManutencao,
        UserSimpleDTO usuario
) {
}
