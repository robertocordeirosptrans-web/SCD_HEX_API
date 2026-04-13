package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record GroupUserResponseDTO(
        Long idUsuario,
        String codGrupo,
        String codStatus,
        LocalDateTime dtModi,
        UserSimpleDTO usuario,
        UserSimpleDTO usuarioManutencao) {
}
