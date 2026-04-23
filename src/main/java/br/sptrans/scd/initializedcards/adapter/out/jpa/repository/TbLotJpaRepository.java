package br.sptrans.scd.initializedcards.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.TbLotSCDEntityJpa;

public interface TbLotJpaRepository extends JpaRepository<TbLotSCDEntityJpa, Long>, JpaSpecificationExecutor<TbLotSCDEntityJpa> {

    @Query("SELECT t FROM TbLotSCDEntityJpa t WHERE t.status = '1'")
    List<TbLotSCDEntityJpa> findDisponiveis(Sort sort);
}
