package br.sptrans.scd.creditrequest.application.port.out.projection;

import java.math.BigDecimal;

/**
 * Projection for product period report showing daily item counts and financial
 * totals. Used for generating reports with daily breakdown of items per
 * product.
 */
public interface ProductPeriodReportProjection {

    /**
     * Product code
     */
    String getCodProduto();

    /**
     * Day 1 item count
     */
    Long getDia01();

    /**
     * Day 2 item count
     */
    Long getDia02();

    /**
     * Day 3 item count
     */
    Long getDia03();

    /**
     * Day 4 item count
     */
    Long getDia04();

    /**
     * Day 5 item count
     */
    Long getDia05();

    /**
     * Day 6 item count
     */
    Long getDia06();

    /**
     * Day 7 item count
     */
    Long getDia07();

    /**
     * Day 8 item count
     */
    Long getDia08();

    /**
     * Day 9 item count
     */
    Long getDia09();

    /**
     * Day 10 item count
     */
    Long getDia10();

    /**
     * Day 11 item count
     */
    Long getDia11();

    /**
     * Day 12 item count
     */
    Long getDia12();

    /**
     * Day 13 item count
     */
    Long getDia13();

    /**
     * Day 14 item count
     */
    Long getDia14();

    /**
     * Day 15 item count
     */
    Long getDia15();

    /**
     * Day 16 item count
     */
    Long getDia16();

    /**
     * Day 17 item count
     */
    Long getDia17();

    /**
     * Day 18 item count
     */
    Long getDia18();

    /**
     * Day 19 item count
     */
    Long getDia19();

    /**
     * Day 20 item count
     */
    Long getDia20();

    /**
     * Day 21 item count
     */
    Long getDia21();

    /**
     * Day 22 item count
     */
    Long getDia22();

    /**
     * Day 23 item count
     */
    Long getDia23();

    /**
     * Day 24 item count
     */
    Long getDia24();

    /**
     * Day 25 item count
     */
    Long getDia25();

    /**
     * Day 26 item count
     */
    Long getDia26();

    /**
     * Day 27 item count
     */
    Long getDia27();

    /**
     * Day 28 item count
     */
    Long getDia28();

    /**
     * Day 29 item count
     */
    Long getDia29();

    /**
     * Day 30 item count
     */
    Long getDia30();

    /**
     * Day 31 item count (for months with 31 days)
     */
    Long getDia31();

    /**
     * Total items in the period
     */
    Long getTotalItensPeriodo();

    /**
     * Total financial value for the period
     */
    BigDecimal getVlTotalPeriodo();

    /**
     * Total paid value for the period
     */
    BigDecimal getVlPagoPeriodo();
}
