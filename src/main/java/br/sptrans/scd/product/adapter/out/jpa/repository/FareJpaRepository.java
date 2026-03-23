package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.out.jpa.entity.FareEntityJpa;

public interface FareJpaRepository extends JpaRepository<FareEntityJpa, String>, JpaSpecificationExecutor<FareEntityJpa>{
    
}
