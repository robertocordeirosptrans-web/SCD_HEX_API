package br.sptrans.scd.channel.adapter.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.channel.adapter.out.persistence.entity.AgreementValidityEntityJpa;
import br.sptrans.scd.channel.adapter.out.persistence.entity.AgreementValidityKeyEntityJpa;

public interface AgreementValidityJpaRepository extends JpaRepository<AgreementValidityEntityJpa, AgreementValidityKeyEntityJpa>, JpaSpecificationExecutor<AgreementValidityEntityJpa> {

    @Query("SELECT a FROM AgreementValidityEntityJpa a WHERE a.id.codCanal = :codCanal AND a.id.codProduto = :codProduto")
    Optional<AgreementValidityEntityJpa> findByCodCanalAndCodProduto(@Param("codCanal") String codCanal, @Param("codProduto") String codProduto);

    @Query("SELECT a FROM AgreementValidityEntityJpa a")
    List<AgreementValidityEntityJpa> findAllAgreementValidity();

    @Query("SELECT a FROM AgreementValidityEntityJpa a")
    Page<AgreementValidityEntityJpa> findAllAgreementValidity(Pageable pageable);

    @Query("SELECT a FROM AgreementValidityEntityJpa a WHERE a.id.codCanal = :codCanal")
    Page<AgreementValidityEntityJpa> findByCodCanal(@Param("codCanal") String codCanal, Pageable pageable);

    @Query("SELECT a FROM AgreementValidityEntityJpa a WHERE a.id.codProduto = :codProduto")
    Page<AgreementValidityEntityJpa> findByCodProduto(@Param("codProduto") String codProduto, Pageable pageable);

    @Query("SELECT COUNT(a) > 0 FROM AgreementValidityEntityJpa a WHERE a.id.codCanal = :codCanal AND a.id.codProduto = :codProduto")
    boolean existsByCodCanalAndCodProduto(@Param("codCanal") String codCanal, @Param("codProduto") String codProduto);


}
