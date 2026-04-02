package br.sptrans.scd.auth.application.port.out;

import java.util.Optional;

import br.sptrans.scd.auth.domain.Functionality;

public interface FunctionalityPort {

    Optional<Functionality> findById_CodSistemaAndId_CodModuloAndId_CodRotinaAndId_CodFuncionalidade(String codSistema, String codModulo, String codRotina, String codFuncionalidade);
}
