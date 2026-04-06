package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.out.persistence.entity.ProductVersionEntityJpa;

public interface ProductVersionJpaRepository extends JpaRepository<ProductVersionEntityJpa, String>, JpaSpecificationExecutor<ProductVersionEntityJpa>{
    
}
