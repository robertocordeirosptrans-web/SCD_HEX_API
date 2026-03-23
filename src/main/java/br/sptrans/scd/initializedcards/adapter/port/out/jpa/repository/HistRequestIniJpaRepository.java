package br.sptrans.scd.initializedcards.adapter.port.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity.HistRICEntityJpa;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity.HistRICEntityJpaKey;

public interface HistRequestIniJpaRepository extends JpaRepository<HistRICEntityJpa, HistRICEntityJpaKey>, JpaSpecificationExecutor<HistRICEntityJpa> {
    
}
