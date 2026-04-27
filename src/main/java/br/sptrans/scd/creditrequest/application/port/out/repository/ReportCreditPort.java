package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;

import br.sptrans.scd.creditrequest.application.port.out.projection.ProductPeriodReportProjection;

public interface ReportCreditPort {
    List<ProductPeriodReportProjection> findProductPeriodReport(
            String codCanal, String dataInicio, String dataFim);

    List<ProductPeriodReportProjection> findProductPeriodReportWithProductFilter(
            String codCanal, String dataInicio, String dataFim, List<String> codProdutos);
}
