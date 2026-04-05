package br.sptrans.scd.initializedcards.adapter.port.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.TbLotSCDEntityJpa;

public interface TbLotJpaRepository extends JpaRepository<TbLotSCDEntityJpa, Long>, JpaSpecificationExecutor<TbLotSCDEntityJpa> {
    
}
