package br.sptrans.scd.creditrequest.adapter.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestItemsKeyEJpa;

public interface HistCreditItemJpaRepository extends JpaRepository<HistCreditRequestItemsEJpa, HistCreditRequestItemsKeyEJpa>, JpaSpecificationExecutor<HistCreditRequestItemsEJpa> {

	@Query("SELECT h FROM HistCreditRequestItemsEJpa h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.numSolicitacaoItem = :numSolicitacaoItem AND h.id.codCanal = :codCanal AND h.id.seqHistSdis = :seqHistSdis")
	Optional<HistCreditRequestItemsEJpa> findByIdAllFields(@Param("numSolicitacao") Long numSolicitacao,
														  @Param("numSolicitacaoItem") Long numSolicitacaoItem,
														  @Param("codCanal") String codCanal,
														  @Param("seqHistSdis") Long seqHistSdis);

	@Query("SELECT h FROM HistCreditRequestItemsEJpa h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.codCanal = :codCanal ORDER BY h.id.seqHistSdis DESC")
	List<HistCreditRequestItemsEJpa> findByNumSolicitacaoAndCodCanal(@Param("numSolicitacao") Long numSolicitacao,
																	@Param("codCanal") String codCanal);

	@Query("SELECT h FROM HistCreditRequestItemsEJpa h ORDER BY h.id.numSolicitacao, h.id.numSolicitacaoItem, h.id.seqHistSdis DESC")
	List<HistCreditRequestItemsEJpa> findAllOrdered();

	@Query("SELECT COUNT(h) FROM HistCreditRequestItemsEJpa h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.numSolicitacaoItem = :numSolicitacaoItem AND h.id.codCanal = :codCanal AND h.id.seqHistSdis = :seqHistSdis")
	long countById(@Param("numSolicitacao") Long numSolicitacao,
				   @Param("numSolicitacaoItem") Long numSolicitacaoItem,
				   @Param("codCanal") String codCanal,
				   @Param("seqHistSdis") Long seqHistSdis);

	@Query("SELECT COALESCE(MAX(h.id.seqHistSdis), 0) FROM HistCreditRequestItemsEJpa h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.numSolicitacaoItem = :numSolicitacaoItem AND h.id.codCanal = :codCanal")
	Long findMaxSeqHistSdis(@Param("numSolicitacao") Long numSolicitacao,
							@Param("numSolicitacaoItem") Long numSolicitacaoItem,
							@Param("codCanal") String codCanal);

	@Query("SELECT h FROM HistCreditRequestItemsEJpa h WHERE h.id.numSolicitacao = :numSolicitacao AND h.id.numSolicitacaoItem = :numSolicitacaoItem AND h.id.codCanal = :codCanal ORDER BY h.id.seqHistSdis DESC")
	List<HistCreditRequestItemsEJpa> findLatestByItem(@Param("numSolicitacao") Long numSolicitacao,
													 @Param("numSolicitacaoItem") Long numSolicitacaoItem,
													 @Param("codCanal") String codCanal);
}

