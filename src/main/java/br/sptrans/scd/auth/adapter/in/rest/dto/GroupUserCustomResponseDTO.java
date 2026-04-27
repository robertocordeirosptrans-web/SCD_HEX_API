package br.sptrans.scd.auth.adapter.in.rest.dto;

public record GroupUserCustomResponseDTO(
    String codLogin,
    String nomUsuario,
    String nomDepartamento,
    String nomEmail,
    String codStatus
) {}