package br.sptrans.scd.initializedcards.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RequestLotSCPEntityJpa;
import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RequestLotSCPEntityJpaKey;


public interface RequestLotJpaRepository extends JpaRepository<RequestLotSCPEntityJpa, RequestLotSCPEntityJpaKey>, JpaSpecificationExecutor<RequestLotSCPEntityJpa> {
    
}
