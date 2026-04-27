package br.sptrans.scd.initializedcards.adapter.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.HistRICEntityJpa;
import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.HistRICEntityJpaKey;

public interface HistRequestIniJpaRepository
        extends JpaRepository<HistRICEntityJpa, HistRICEntityJpaKey>, JpaSpecificationExecutor<HistRICEntityJpa> {

    @Query("SELECT h FROM HistRICEntityJpa h WHERE h.id.codCanal = :codCanal AND h.id.nrSolicitacao = :nrSolicitacao AND h.id.seqHistSolicCartaoIni = :seqHistSolicCartaoIni")
    Optional<HistRICEntityJpa> findByHistId(
            @Param("codCanal") String codCanal,
            @Param("nrSolicitacao") Long nrSolicitacao,
            @Param("seqHistSolicCartaoIni") Long seqHistSolicCartaoIni);

    @Query("SELECT h FROM HistRICEntityJpa h WHERE h.id.codCanal = :codCanal AND h.id.nrSolicitacao = :nrSolicitacao ORDER BY h.id.seqHistSolicCartaoIni")
    List<HistRICEntityJpa> findAllHistByCodCanalAndNrSolicitacao(
            @Param("codCanal") String codCanal,
            @Param("nrSolicitacao") Long nrSolicitacao);

    @Query("SELECT COALESCE(MAX(h.id.seqHistSolicCartaoIni), 0) + 1 FROM HistRICEntityJpa h WHERE h.id.codCanal = :codCanal AND h.id.nrSolicitacao = :nrSolicitacao")
    Long nextSeqHist(
            @Param("codCanal") String codCanal,
            @Param("nrSolicitacao") Long nrSolicitacao);
}

