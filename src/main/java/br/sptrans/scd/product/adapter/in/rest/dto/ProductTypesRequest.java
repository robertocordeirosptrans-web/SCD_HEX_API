package br.sptrans.scd.product.adapter.in.rest.dto;



import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;

public record ProductTypesRequest(
    @NotBlank
    @Size(max = 60) String desTipoProduto
) {}
