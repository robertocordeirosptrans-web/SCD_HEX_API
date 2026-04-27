package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record ProfileResponseDTO(
        String codPerfil,
        String nomPerfil,
        String codStatus,
        LocalDateTime dtModi,
        UserSimpleDTO usuarioManutencao) {
}
