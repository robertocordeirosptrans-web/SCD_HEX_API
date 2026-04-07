package br.sptrans.scd.product.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFee {
        private Long codTaxaSrv;

    private Byte recInicial;

    private Byte recFinal;

    private BigDecimal valFixo;

    private BigDecimal valPercentual;

    private BigDecimal valMinimo;

     private Fee taxa;

    public BigDecimal calculateValue(BigDecimal base) {
        BigDecimal fixo = valFixo != null ? valFixo : BigDecimal.ZERO;
        BigDecimal percentual = valPercentual != null ? valPercentual : BigDecimal.ZERO;
        BigDecimal baseCalculo = base != null ? base : BigDecimal.ZERO;
        BigDecimal calculado = fixo.add(percentual.multiply(baseCalculo)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        return (valMinimo != null && valMinimo.compareTo(calculado) > 0) ? valMinimo : calculado;
    }
}
