package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record GrupoResponseDTO(
        String codGrupo,
        String nomGrupo,
        String codStatus,
        LocalDateTime dtModi,
        UserSimpleDTO usuarioManutencao) {
}
