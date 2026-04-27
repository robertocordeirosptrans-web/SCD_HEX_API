package br.sptrans.scd.initializedcards.adapter.out.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RICEntityJpa;
import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RICEntityJpaKey;

public interface RequestIniJpaRepository
        extends JpaRepository<RICEntityJpa, RICEntityJpaKey>, JpaSpecificationExecutor<RICEntityJpa> {

    @Query("SELECT r FROM RICEntityJpa r WHERE r.id.codCanal = :codCanal AND r.id.nrSolicitacao = :nrSolicitacao")
    Optional<RICEntityJpa> findByCodCanalAndNrSolicitacao(
            @Param("codCanal") String codCanal,
            @Param("nrSolicitacao") Long nrSolicitacao);

    @Query("SELECT r FROM RICEntityJpa r WHERE r.id.codCanal = :codCanal AND r.id.nrSolicitacao = :nrSolicitacao AND r.codAdquirente = :codAdquirente")
    Optional<RICEntityJpa> findByCodCanalAndNrSolicitacaoAndCodAdquirente(
            @Param("codCanal") String codCanal,
            @Param("nrSolicitacao") Long nrSolicitacao,
            @Param("codAdquirente") String codAdquirente);

    @Query("SELECT COALESCE(MAX(r.id.nrSolicitacao), 0) + 1 FROM RICEntityJpa r WHERE r.id.codTipoCanal = :codTipoCanal AND r.id.codCanal = :codCanal")
    Long nextNrSolicitacao(
            @Param("codTipoCanal") String codTipoCanal,
            @Param("codCanal") String codCanal);
}

