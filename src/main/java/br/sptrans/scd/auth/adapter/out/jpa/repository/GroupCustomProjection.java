package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.time.LocalDateTime;

public interface GroupCustomProjection {
    String getCodGrupo();
    String getNomGrupo();
    LocalDateTime getDtModi();
    String getCodStatus();
}
