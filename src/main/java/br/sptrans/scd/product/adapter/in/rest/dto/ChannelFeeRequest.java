package br.sptrans.scd.product.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record ChannelFeeRequest(
    @NotNull String codCanal,
    @NotNull String codProduto,
    BigDecimal valInicio,
    BigDecimal valFim,
    BigDecimal valPercentual,
    @NotNull LocalDateTime dtInicio,
    LocalDateTime dtFim,
    @NotNull LocalDateTime dtManutencao,
    Long idUsuarioManutencao
) {}
