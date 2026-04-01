package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sptrans.scd.product.adapter.port.out.persistence.entity.ServiceFeeEntityJpa;

public interface ServiceFeeJpaRepository extends JpaRepository<ServiceFeeEntityJpa, Long> {
}
