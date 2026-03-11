package br.sptrans.scd.auth.adapter.port.in.rest.dto;

import java.util.Date;

public record UserRequestDTO(
    String codLogin,
    String nomUsuario,
    String nomEmail,
    String codCpf,
    String codRg,
    String numDiasSemanasPermitidos,
    Date dtJornadaIni,
    Date dtJornadaFim,
    Long idUsuarioLogado
) {}
