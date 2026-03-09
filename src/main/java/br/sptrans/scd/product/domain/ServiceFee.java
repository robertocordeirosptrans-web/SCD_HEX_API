package br.sptrans.scd.product.domain;

import java.math.BigDecimal;

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

     
}
