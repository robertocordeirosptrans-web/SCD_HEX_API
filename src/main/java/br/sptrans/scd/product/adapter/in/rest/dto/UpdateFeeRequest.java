package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateFeeRequest(
    @Size(max = 20) String desTaxa,
    @NotNull LocalDateTime dtFim,
    @NotNull AdministrativeFeeRequest taxaAdministrativa,
    @NotNull ServiceFeeRequest taxaServico
) {}
