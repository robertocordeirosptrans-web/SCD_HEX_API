package br.sptrans.scd.auth.adapter.port.in.rest.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(
    Long idUsuario,
    String codLogin,
    String nomUsuario,
    String nomEmail,
    String codCpf,
    String codRg,
    String codStatus,
    LocalDateTime dtCriacao,
    LocalDateTime dtModi,
    LocalDateTime dtExpiraSenha
) {}
