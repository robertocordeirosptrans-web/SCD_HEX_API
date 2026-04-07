package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FareRequest(
    @NotBlank @Size(max = 20) String codTarifa,
    @NotBlank @Size(max = 20) String codProduto,
    @NotNull LocalDateTime dtVigenciaInicio,
    @NotNull LocalDateTime dtVigenciaFim,
    @Size(max = 60) String desTarifa,
    Long idUsuarioCadastro,
    Long valTarifa,
    @NotNull LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    Long idUsuarioManutencao,
    @NotBlank String codStatus
) {}
