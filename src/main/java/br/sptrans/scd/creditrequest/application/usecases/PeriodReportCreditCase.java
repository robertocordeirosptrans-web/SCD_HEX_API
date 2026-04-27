package br.sptrans.scd.creditrequest.application.usecases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.application.port.out.projection.ProductPeriodReportProjection;
import br.sptrans.scd.creditrequest.application.port.out.repository.ReportCreditPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PeriodReportCreditCase {
    /**
     * Generates a product period report for the given canal and date range.
     *
     * @param codCanal    Canal code filter
     * @param dataInicio  Start date of the period
     * @param dataFim     End date of the period
     * @param codProdutos Optional list of product codes to filter (null or empty
     *                    means all products)
     * @return List of product period report projections
     */

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger log = LoggerFactory.getLogger(PeriodReportCreditCase.class);
    private final ReportCreditPort productReportRepository;

    public List<ProductPeriodReportProjection> execute(String codCanal, LocalDateTime dataInicio, LocalDateTime dataFim,
            List<String> codProdutos) {
        long daysBetween = ChronoUnit.DAYS.between(dataInicio, dataFim);
        if (daysBetween >= 31) {
            throw new IllegalArgumentException(
                    "O intervalo de datas não pode exceder 31 dias (incluindo as datas de início e término).");
        }

        log.info(
                "Gerando relatório de período de produto para canal: {}, período: {} a {}, produtos: {} (executando consulta - não do cache)",
                codCanal, dataInicio, dataFim, codProdutos != null && !codProdutos.isEmpty() ? codProdutos : "all");

        String dataInicioStr = dataInicio.format(DATE_FORMATTER);
        String dataFimStr = dataFim.format(DATE_FORMATTER);

        List<ProductPeriodReportProjection> results;

        if (codProdutos != null && !codProdutos.isEmpty()) {
            results = productReportRepository.findProductPeriodReportWithProductFilter(
                    codCanal, dataInicioStr, dataFimStr, codProdutos);
        } else {
            results = productReportRepository.findProductPeriodReport(codCanal, dataInicioStr, dataFimStr);
        }

        log.info("Relatório de período do produto gerado com sucesso. Encontrados {} produtos", results.size());

        return results;
    }
}
