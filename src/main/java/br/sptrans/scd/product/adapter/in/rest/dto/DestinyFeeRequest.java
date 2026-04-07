package br.sptrans.scd.product.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DestinyFeeRequest(
    @NotNull Long codTaxaDes,
    @NotNull @Size(max = 20) String codCanalDestino
) {}
