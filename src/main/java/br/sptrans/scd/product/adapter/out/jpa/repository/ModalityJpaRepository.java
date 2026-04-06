package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.out.persistence.entity.ModalityEntityJpa;

public interface ModalityJpaRepository extends JpaRepository<ModalityEntityJpa, String>, JpaSpecificationExecutor<ModalityEntityJpa>{

    Page<ModalityEntityJpa> findByCodStatus(String codStatus, Pageable pageable);
}
