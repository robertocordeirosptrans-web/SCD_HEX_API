
package br.sptrans.scd.product.domain.vo;
import java.math.BigDecimal;

/**
 * Value Object para representar o cálculo de taxas administrativas e de serviço.
 */
//
public class TaxaCalculada {
    private final BigDecimal valorTaxaAdm;
    private final BigDecimal valorTaxaServ;

    public BigDecimal getValorTaxaAdm() {
        return valorTaxaAdm;
    }

    public BigDecimal getValorTaxaServ() {
        return valorTaxaServ;
    }

    public TaxaCalculada(BigDecimal valorTaxaAdm, BigDecimal valorTaxaServ) {
        this.valorTaxaAdm = valorTaxaAdm;
        this.valorTaxaServ = valorTaxaServ;
    }

    public static TaxaCalculada isenta() {
        return new TaxaCalculada(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public String toString() {
        return "TaxaCalculada{" +
                "valorTaxaAdm=" + valorTaxaAdm +
                ", valorTaxaServ=" + valorTaxaServ +
                '}';
    }
}
