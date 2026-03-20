package br.sptrans.scd.auth.adapter.port.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.auth.adapter.port.out.jpa.entity.FunctionalityEntityJpa;
import br.sptrans.scd.auth.adapter.port.out.jpa.entity.FunctionalityEntityJpaKey;

public interface FunctionalityJpaRepository extends JpaRepository<FunctionalityEntityJpa, FunctionalityEntityJpaKey>, JpaSpecificationExecutor<FunctionalityEntityJpa> {
    
}
