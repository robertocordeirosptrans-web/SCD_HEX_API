package br.sptrans.scd.product.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterFareRequest(
    @NotNull @Size(max = 20) String codProduto,
    @Size(max = 20) String codVersao,
    @NotNull @Size(max = 20) String codCanal,
    @Size(max = 60) String desTarifa,
    @NotNull BigDecimal valTarifa,
    @NotNull LocalDateTime dtInicio,
    @NotNull LocalDateTime dtFim,
    @NotNull Long idUsuario
) {}
