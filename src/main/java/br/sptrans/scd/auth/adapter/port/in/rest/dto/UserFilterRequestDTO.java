package br.sptrans.scd.auth.adapter.port.in.rest.dto;

public record UserFilterRequestDTO(
    String nome,
    String email,
    String perfil,
    String status
) {
}
