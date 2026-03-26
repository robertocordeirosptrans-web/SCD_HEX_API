package br.sptrans.scd.channel.adapter.port.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.channel.adapter.port.out.jpa.entity.AgreementValidityEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.AgreementValidityKeyEntityJpa;

public interface AgreementValidityJpaRepository extends JpaRepository<AgreementValidityEntityJpa, AgreementValidityKeyEntityJpa>, JpaSpecificationExecutor<AgreementValidityEntityJpa> {

    @Query("SELECT a FROM AgreementValidityEntityJpa a WHERE a.id.codCanal = :codCanal AND a.id.codProduto = :codProduto")
    Optional<AgreementValidityEntityJpa> findByCodCanalAndCodProduto(@Param("codCanal") String codCanal, @Param("codProduto") String codProduto);

    @Query("SELECT a FROM AgreementValidityEntityJpa a")
    List<AgreementValidityEntityJpa> findAllAgreementValidity();

    @Query("SELECT COUNT(a) > 0 FROM AgreementValidityEntityJpa a WHERE a.id.codCanal = :codCanal AND a.id.codProduto = :codProduto")
    boolean existsByCodCanalAndCodProduto(@Param("codCanal") String codCanal, @Param("codProduto") String codProduto);


}
