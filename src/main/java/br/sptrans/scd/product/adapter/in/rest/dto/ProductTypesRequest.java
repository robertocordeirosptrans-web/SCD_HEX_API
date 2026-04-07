package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductTypesRequest(
    @NotBlank @Size(max = 20) String codTipoProduto,
    @Size(max = 60) String desTipoProduto,
    @NotBlank @Size(max = 1) String codStatus,
    @NotNull LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    Long idUsuarioCadastro,
    Long idUsuarioManutencao
) {}
