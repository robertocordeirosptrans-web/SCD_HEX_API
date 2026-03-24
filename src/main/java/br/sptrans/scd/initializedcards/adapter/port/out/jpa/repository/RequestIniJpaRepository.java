package br.sptrans.scd.initializedcards.adapter.port.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity.RICEntityJpa;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity.RICEntityJpaKey;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface RequestIniJpaRepository extends JpaRepository<RICEntityJpa, RICEntityJpaKey>, JpaSpecificationExecutor<RICEntityJpa> {

	@Query("SELECT r FROM RICEntityJpa r WHERE r.id.codCanal = :codCanal AND r.id.nrSolicitacao = :nrSolicitacao")
	Optional<RICEntityJpa> findByCodCanalAndNrSolicitacao(@Param("codCanal") String codCanal, @Param("nrSolicitacao") Long nrSolicitacao);

	@Query("SELECT r FROM RICEntityJpa r WHERE r.id.codCanal = :codCanal AND r.id.nrSolicitacao = :nrSolicitacao AND r.codAdquirente = :codAdquirente")
	Optional<RICEntityJpa> findByCodCanalAndNrSolicitacaoAndCodAdquirente(@Param("codCanal") String codCanal, @Param("nrSolicitacao") Long nrSolicitacao, @Param("codAdquirente") String codAdquirente);
}
