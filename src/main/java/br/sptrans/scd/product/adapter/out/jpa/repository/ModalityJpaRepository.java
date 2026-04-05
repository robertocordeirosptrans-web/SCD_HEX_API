package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.port.out.persistence.entity.ModalityEntityJpa;

public interface ModalityJpaRepository extends JpaRepository<ModalityEntityJpa, String>, JpaSpecificationExecutor<ModalityEntityJpa>{
    
}
