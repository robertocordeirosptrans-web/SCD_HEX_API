package br.sptrans.scd.initializedcards.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RequestLotSCPEntityJpa;
import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RequestLotSCPEntityJpaKey;

public interface RequestLotJpaRepository
        extends JpaRepository<RequestLotSCPEntityJpa, RequestLotSCPEntityJpaKey>,
        JpaSpecificationExecutor<RequestLotSCPEntityJpa> {

    @Query("SELECT r FROM RequestLotSCPEntityJpa r WHERE r.id.codCanal = :codCanal AND r.id.nrSolicitacao = :nrSolicitacao")
    List<RequestLotSCPEntityJpa> findAllByCodCanalAndNrSolicitacao(
            @Param("codCanal") String codCanal,
            @Param("nrSolicitacao") Long nrSolicitacao);

    @Modifying
    @Query("DELETE FROM RequestLotSCPEntityJpa r WHERE r.id.codCanal = :codCanal AND r.id.nrSolicitacao = :nrSolicitacao")
    void deleteAllByCodCanalAndNrSolicitacao(
            @Param("codCanal") String codCanal,
            @Param("nrSolicitacao") Long nrSolicitacao);
}

