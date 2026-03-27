package br.sptrans.scd.creditrequest.adapter.port.out.jpa.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.RechargeLogEJpa;

public interface RechargeLogJpaRepository extends JpaRepository<RechargeLogEJpa, Integer>, JpaSpecificationExecutor<RechargeLogEJpa> {

    @Query("SELECT r FROM RechargeLogEJpa r WHERE r.numLogicoCartao = :numLogicoCartao")
    Optional<RechargeLogEJpa> findByNumLogicoCartao(String numLogicoCartao);
}
