

package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpaKey;

public interface FunctionalityJpaRepository extends JpaRepository<FunctionalityEntityJpa, FunctionalityEntityJpaKey>, JpaSpecificationExecutor<FunctionalityEntityJpa> {

    @Query("SELECT f FROM FunctionalityEntityJpa f WHERE f.codStatus = 'A'")
    List<FunctionalityEntityJpa> findAllActive();
}
