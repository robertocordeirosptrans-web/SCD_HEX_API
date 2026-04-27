package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDate;

public record GroupProfileResponseDTO(
        String codGrupo,
        String codPerfil,
        String codStatus,
        LocalDate dtModi,
        UserSimpleDTO usuarioManutencao) {
}
