package br.sptrans.scd.product.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.sptrans.scd.creditrequest.application.port.out.projection.CardsTypeProjection;
import br.sptrans.scd.product.adapter.out.persistence.entity.CardsTypeEntityJpa;

public interface CardTypeRepository extends JpaRepository<CardsTypeEntityJpa, String> {

    @Query(value = "SELECT NI_IDTPCARTAO AS codTipoCartao, " +
            "       VC_DESC AS desTipoCartao " +
            "FROM SPTRANS.TB_TPCARTAO@DBLINK_SCP " + // ← COM SCHEMA!
            "ORDER BY VC_DESC", nativeQuery = true)
    List<CardsTypeProjection> findAllViaDblink();

}
