package br.sptrans.scd.creditrequest.adapter.out.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.RechargeLogEntity;

public interface RechargeLogJpaRepository extends JpaRepository<RechargeLogEntity, Integer>, JpaSpecificationExecutor<RechargeLogEntity> {

    @Query("SELECT r FROM RechargeLogEntity r WHERE r.numLogicoCartao = :numLogicoCartao")
    Optional<RechargeLogEntity> findByNumLogicoCartao(String numLogicoCartao);

    @Query(value = "SELECT SQ_LOG_RECARGAS.NEXTVAL FROM DUAL",
            nativeQuery = true)
    int nextSeqRecarga();

}
