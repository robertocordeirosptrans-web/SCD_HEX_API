package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateFareRequest(
    @Size(max = 60) String desTarifa,
    @NotNull LocalDateTime dtFim,
    @NotNull Long idUsuario
) {}
