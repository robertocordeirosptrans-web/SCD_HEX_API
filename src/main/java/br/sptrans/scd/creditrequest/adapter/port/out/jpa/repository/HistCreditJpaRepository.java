package br.sptrans.scd.creditrequest.adapter.port.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.HistCreditRequestEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.HistCreditRequestKeyEJpa;

import java.util.List;

public interface HistCreditJpaRepository extends JpaRepository<HistCreditRequestEJpa, HistCreditRequestKeyEJpa>, JpaSpecificationExecutor<HistCreditRequestEJpa> {
	@Query("SELECT h FROM HistCreditRequestEJpa h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.codCanal = :codCanal ORDER BY h.id.seqHistSdis DESC")
	List<HistCreditRequestEJpa> findByNumSolicitacaoAndCodCanal(@Param("numSolicitacao") Long numSolicitacao, @Param("codCanal") String codCanal);

	@Query("SELECT COALESCE(MAX(h.id.seqHistSdis), 0) FROM HistCreditRequestEJpa h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.codCanal = :codCanal")
	Long findMaxSeqHistSdis(@Param("numSolicitacao") Long numSolicitacao, @Param("codCanal") String codCanal);

	@Query("SELECT h FROM HistCreditRequestEJpa h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.codCanal = :codCanal ORDER BY h.id.seqHistSdis DESC")
	List<HistCreditRequestEJpa> findLatestBySolicitacao(@Param("numSolicitacao") Long numSolicitacao, @Param("codCanal") String codCanal);
}
