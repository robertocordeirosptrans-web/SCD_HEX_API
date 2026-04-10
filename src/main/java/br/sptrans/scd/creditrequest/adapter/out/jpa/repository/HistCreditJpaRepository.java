package br.sptrans.scd.creditrequest.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestEntityKey;

public interface HistCreditJpaRepository extends JpaRepository<HistCreditRequestEntity, HistCreditRequestEntityKey>, JpaSpecificationExecutor<HistCreditRequestEntity> {
	@Query("SELECT h FROM HistCreditRequestEntity h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.codCanal = :codCanal ORDER BY h.id.seqHistSdis DESC")
	List<HistCreditRequestEntity> findByNumSolicitacaoAndCodCanal(@Param("numSolicitacao") Long numSolicitacao, @Param("codCanal") String codCanal);

	@Query("SELECT COALESCE(MAX(h.id.seqHistSdis), 0) FROM HistCreditRequestEntity h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.codCanal = :codCanal")
	Long findMaxSeqHistSdis(@Param("numSolicitacao") Long numSolicitacao, @Param("codCanal") String codCanal);

	@Query("SELECT h FROM HistCreditRequestEntity h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.codCanal = :codCanal ORDER BY h.id.seqHistSdis DESC")
	List<HistCreditRequestEntity> findLatestBySolicitacao(@Param("numSolicitacao") Long numSolicitacao, @Param("codCanal") String codCanal);
}
