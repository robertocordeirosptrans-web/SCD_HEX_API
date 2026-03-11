package br.sptrans.scd.auth.application.port.out;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sptrans.scd.auth.adapter.out.jpa.entity.FunctionalityEntityJpa;
import br.sptrans.scd.auth.adapter.out.jpa.entity.FunctionalityEntityJpaKey;



public interface FunctionalityRepository extends JpaRepository<FunctionalityEntityJpa, FunctionalityEntityJpaKey> {

    Optional<FunctionalityEntityJpa> findById_CodSistemaAndId_CodModuloAndId_CodRotinaAndId_CodFuncionalidade(String codSistema, String codModulo, String codRotina, String codFuncionalidade);
}
