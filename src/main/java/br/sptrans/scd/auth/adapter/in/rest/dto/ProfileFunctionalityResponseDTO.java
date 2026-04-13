package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDate;

public record ProfileFunctionalityResponseDTO(
        String codSistema,
        String codModulo,
        String codRotina,
        String codFuncionalidade,
        String codPerfil,
        LocalDate dtInicioValidade,
        UserSimpleDTO usuarioManutencao) {
}
