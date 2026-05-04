package br.sptrans.scd.initializedcards.adapter.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.TbLotSCDEntityJpa;

public interface TbLotJpaRepository extends JpaRepository<TbLotSCDEntityJpa, Long>, JpaSpecificationExecutor<TbLotSCDEntityJpa> {

    @Query("SELECT t FROM TbLotSCDEntityJpa t " +
           "WHERE t.status = '1' " +
           "AND t.codTipoCartao = :codTipoCartao " +
           "AND EXISTS (" +
           "    SELECT 1 FROM ProductEntityJpa p " +
           "    WHERE p.codTipoCartao = t.codTipoCartao" +
           ")")
    Page<TbLotSCDEntityJpa> findDisponiveis(
            @Param("codTipoCartao") Long codTipoCartao,
            Pageable pageable);

    @Modifying
    @Query("UPDATE TbLotSCDEntityJpa t SET t.status = :status WHERE t.idLote IN :ids")
    void updateStatusByIds(@Param("ids") java.util.List<Long> ids, @Param("status") String status);
}
