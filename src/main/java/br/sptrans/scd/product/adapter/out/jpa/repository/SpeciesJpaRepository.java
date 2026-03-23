package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.out.jpa.entity.SpeciesEntityJpa;

public interface SpeciesJpaRepository extends JpaRepository<SpeciesEntityJpa, String> , JpaSpecificationExecutor<SpeciesEntityJpa>{
    
}
