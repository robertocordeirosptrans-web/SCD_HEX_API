package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;

public interface ProfileFunctionalityProjectionDTO {
    String getCodSistema();
    String getCodModulo();
    String getCodRotina();
    String getCodFuncionalidade();
    String getNomFuncionalidade();
    LocalDateTime getDtModi();
    String getFlgEvento();
    String getFlgMonitoracao();
}
