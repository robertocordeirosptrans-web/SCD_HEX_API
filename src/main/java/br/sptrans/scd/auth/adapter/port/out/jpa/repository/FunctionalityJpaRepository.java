

package br.sptrans.scd.auth.adapter.port.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import br.sptrans.scd.auth.adapter.port.out.jpa.entity.FunctionalityEntityJpa;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import br.sptrans.scd.auth.adapter.port.out.jpa.entity.FunctionalityEntityJpaKey;

public interface FunctionalityJpaRepository extends JpaRepository<FunctionalityEntityJpa, FunctionalityEntityJpaKey>, JpaSpecificationExecutor<FunctionalityEntityJpa> {

    @Query("SELECT f FROM FunctionalityEntityJpa f WHERE f.codStatus = 'A'")
    List<FunctionalityEntityJpa> findAllActive();
}
