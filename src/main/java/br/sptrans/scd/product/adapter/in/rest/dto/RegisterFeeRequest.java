package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterFeeRequest(
    @NotNull @Size(max = 20) String codProduto,
    @NotNull @Size(max = 20) String codCanal,
    @Size(max = 20) String desTaxa,
    @NotNull LocalDateTime dtInicio,
    @NotNull LocalDateTime dtFim,
    @NotNull AdministrativeFeeRequest taxaAdministrativa,
    @NotNull ServiceFeeRequest taxaServico,
    DestinyFeeRequest taxaDestino
) {}
