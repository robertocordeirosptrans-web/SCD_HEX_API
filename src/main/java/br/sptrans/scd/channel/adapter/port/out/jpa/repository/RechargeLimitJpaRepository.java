package br.sptrans.scd.channel.adapter.port.out.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.channel.adapter.port.out.persistence.entity.RechargeLimitEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.RechargeLimitKeyEntityJpa;

public interface RechargeLimitJpaRepository extends JpaRepository<RechargeLimitEntityJpa, RechargeLimitKeyEntityJpa>, JpaSpecificationExecutor<RechargeLimitEntityJpa> {

    @Query("SELECT r FROM RechargeLimitEntityJpa r WHERE r.id.codCanal = :codCanal AND r.id.codProduto = :codProduto")
    Optional<RechargeLimitEntityJpa> findByCodCanalAndCodProduto(@Param("codCanal") String codCanal, @Param("codProduto") String codProduto);

    @Query("SELECT r FROM RechargeLimitEntityJpa r")
    List<RechargeLimitEntityJpa> findAllRechargeLimits();

    @Query("SELECT r FROM RechargeLimitEntityJpa r WHERE r.id.codCanal = :codCanal")
    List<RechargeLimitEntityJpa> findByCodCanal(@Param("codCanal") String codCanal);

    @Query("SELECT r FROM RechargeLimitEntityJpa r WHERE r.id.codProduto = :codProduto")
    List<RechargeLimitEntityJpa> findByCodProduto(@Param("codProduto") String codProduto);

    @Query("SELECT COUNT(r) > 0 FROM RechargeLimitEntityJpa r WHERE r.id.codCanal = :codCanal AND r.id.codProduto = :codProduto")
    boolean existsByCodCanalAndCodProduto(@Param("codCanal") String codCanal, @Param("codProduto") String codProduto);


}
