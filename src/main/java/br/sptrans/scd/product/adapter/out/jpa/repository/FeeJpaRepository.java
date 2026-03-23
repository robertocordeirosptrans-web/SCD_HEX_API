package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.out.jpa.entity.FeeEntityJpa;

public interface FeeJpaRepository extends JpaRepository<FeeEntityJpa, Long>, JpaSpecificationExecutor<FeeEntityJpa>{
    
}
