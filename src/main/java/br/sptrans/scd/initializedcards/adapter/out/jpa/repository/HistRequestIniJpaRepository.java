package br.sptrans.scd.initializedcards.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.HistRICEntityJpa;
import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.HistRICEntityJpaKey;

import java.util.Optional;

public interface HistRequestIniJpaRepository extends JpaRepository<HistRICEntityJpa, HistRICEntityJpaKey>, JpaSpecificationExecutor<HistRICEntityJpa> {

	@Query("SELECT h FROM HistRICEntityJpa h WHERE h.id.codCanal = :codCanal AND h.id.nrSolicitacao = :nrSolicitacao AND h.id.seqHistSolicCartaoIni = :seqHistSolicCartaoIni")
	Optional<HistRICEntityJpa> findByHistId(@Param("codCanal") String codCanal, @Param("nrSolicitacao") Long nrSolicitacao, @Param("seqHistSolicCartaoIni") Long seqHistSolicCartaoIni);

	@Query("SELECT h FROM HistRICEntityJpa h WHERE h.id.codCanal = :codCanal AND h.id.nrSolicitacao = :nrSolicitacao AND h.codAdquirente = :codAdquirente")
	Optional<HistRICEntityJpa> findByAllHist(@Param("codCanal") String codCanal, @Param("nrSolicitacao") Long nrSolicitacao, @Param("codAdquirente") String codAdquirente);

}
