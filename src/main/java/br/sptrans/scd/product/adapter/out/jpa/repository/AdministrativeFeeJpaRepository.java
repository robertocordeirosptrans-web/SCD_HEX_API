package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.port.out.persistence.entity.AdministrativeFeeEntityJpa;

public interface AdministrativeFeeJpaRepository extends JpaRepository<AdministrativeFeeEntityJpa, Long>, JpaSpecificationExecutor<AdministrativeFeeEntityJpa> {
}
