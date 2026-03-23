package br.sptrans.scd.initializedcards.adapter.port.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity.RICEntityJpa;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity.RICEntityJpaKey;

public interface RequestIniJpaRepository extends JpaRepository<RICEntityJpa, RICEntityJpaKey>, JpaSpecificationExecutor<RICEntityJpa> {
    
}
