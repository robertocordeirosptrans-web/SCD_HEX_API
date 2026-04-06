package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.port.out.persistence.entity.TechnologyEntityJpa;

public interface TechnologyJpaRepository extends JpaRepository<TechnologyEntityJpa, String>, JpaSpecificationExecutor<TechnologyEntityJpa>{

    Page<TechnologyEntityJpa> findByCodStatus(String codStatus, Pageable pageable);
}
