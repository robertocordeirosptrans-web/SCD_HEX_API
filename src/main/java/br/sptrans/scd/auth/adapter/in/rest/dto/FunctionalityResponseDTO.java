package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record FunctionalityResponseDTO(
        String codSistema,
        String codModulo,
        String codRotina,
        String codFuncionalidade,
        Long idUsuarioManutencao,
        String codStatus,
        LocalDateTime dtModi,
        String flgMonitoracao,
        LocalDateTime dtSinc,
        String nomFuncionalidade,
        String flgEvento) {

}
