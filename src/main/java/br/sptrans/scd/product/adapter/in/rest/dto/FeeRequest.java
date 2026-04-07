package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeeRequest(
    @NotNull Long codTaxa,
    @NotNull @Size(max = 20) String codCanal,
    @NotNull @Size(max = 20) String codProduto,
    @NotNull LocalDateTime dtInicio,
    @Size(max = 20) String desTaxa,
    LocalDateTime dtFim
) {}
