package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.application.port.out.projection.ProductPeriodReportProjection;

public interface CreditRequestItemsRepository extends JpaRepository<CreditRequestItemsEJpa, CreditRequestItemsEJpaKey>,
        JpaSpecificationExecutor<CreditRequestItemsEJpa> {

    List<ProductPeriodReportProjection> findProductPeriodReportWithProductFilter(
            String codCanal,
            String dataInicio,
            String dataFim,
            List<String> codProdutos);

    List<CreditRequestItemsEJpa> findByNumSolicitacaoAndCodCanal(
            @Param("numSolicitacao") Long numSolicitacao,
            @Param("codCanal") String codCanal);
}
