package br.sptrans.scd.auth.adapter.in.rest.dto;

public record UserFilterRequestDTO(
    String nomUsuario,
    String nomEmail,
    String codPerfil,
    String codStatus
) {
}
