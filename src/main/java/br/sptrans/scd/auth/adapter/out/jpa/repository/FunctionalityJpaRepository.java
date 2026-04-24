

package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpaKey;

public interface FunctionalityJpaRepository extends JpaRepository<FunctionalityEntityJpa, FunctionalityEntityJpaKey>, JpaSpecificationExecutor<FunctionalityEntityJpa> {

    @Query("SELECT f FROM FunctionalityEntityJpa f WHERE f.codStatus = 'A'")
    List<FunctionalityEntityJpa> findAllActive();

    @Query("SELECT f FROM FunctionalityEntityJpa f WHERE " +
           "(:codSistema IS NULL OR f.id.codSistema = :codSistema) AND " +
           "(:codModulo IS NULL OR f.id.codModulo = :codModulo) AND " +
           "(:nomFuncionalidade IS NULL OR LOWER(f.nomFuncionalidade) LIKE LOWER(CONCAT('%', :nomFuncionalidade, '%')))")
    Page<FunctionalityEntityJpa> findWithFilters(
            @Param("codSistema") String codSistema,
            @Param("codModulo") String codModulo,
            @Param("nomFuncionalidade") String nomFuncionalidade,
            Pageable pageable);
}
