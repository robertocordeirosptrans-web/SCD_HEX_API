package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.port.out.persistence.entity.SpeciesEntityJpa;

public interface SpeciesJpaRepository extends JpaRepository<SpeciesEntityJpa, String> , JpaSpecificationExecutor<SpeciesEntityJpa>{

    Page<SpeciesEntityJpa> findByCodStatus(String codStatus, Pageable pageable);
}
