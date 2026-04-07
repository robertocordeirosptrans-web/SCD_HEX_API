package br.sptrans.scd.product.adapter.in.rest.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record ServiceFeeRequest(
    @NotNull Long codTaxaSrv,
    @NotNull Byte recInicial,
    @NotNull Byte recFinal,
    BigDecimal valFixo,
    BigDecimal valPercentual,
    @NotNull BigDecimal valMinimo
) {}
