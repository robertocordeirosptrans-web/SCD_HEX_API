package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public record SpeciesRequest(
    @Size(max = 20) String codEspecie,
    @Size(max = 60) String desEspecie,
    @Size(max = 1) String codStatus,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    Long idUsuarioCadastro,
    Long idUsuarioManutencao
) {}
