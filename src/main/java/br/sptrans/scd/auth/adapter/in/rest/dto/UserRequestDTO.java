package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;


public record UserRequestDTO(
    String codLogin,
    String nomUsuario,
    String nomEmail,
    String codCpf,
    String codRg,
    String numDiasSemanasPermitidos,
    LocalDateTime dtJornadaIni,
    LocalDateTime dtJornadaFim,
    Long idUsuarioLogado
) {}
