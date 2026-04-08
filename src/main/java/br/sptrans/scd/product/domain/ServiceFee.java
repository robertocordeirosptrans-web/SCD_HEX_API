package br.sptrans.scd.product.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
        BigDecimal minimo = valMinimo != null ? valMinimo : BigDecimal.ZERO;
        BigDecimal baseCalculo = base != null ? base : BigDecimal.ZERO;
        System.out.println("[DEBUG][ServiceFee] codTaxaSrv=" + codTaxaSrv + ", recInicial=" + recInicial + ", recFinal=" + recFinal + ", valFixo=" + fixo + ", valPercentual=" + percentual + ", valMinimo=" + minimo + ", base=" + baseCalculo);
        BigDecimal calculado = fixo.add(percentual.multiply(baseCalculo)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        System.out.println("[DEBUG][ServiceFee] calculado=" + calculado);
        if (minimo.compareTo(calculado) > 0) {
            System.out.println("[DEBUG][ServiceFee] Aplicando mínimo: " + minimo);
            return minimo;
        }
        return calculado;
    }
}
