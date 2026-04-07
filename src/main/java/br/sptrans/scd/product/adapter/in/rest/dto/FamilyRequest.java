package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FamilyRequest(
    @NotBlank @Size(max = 20) String codFamilia,
    @Size(max = 60) String desFamilia,
    String codStatus,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    Long idUsuarioCadastro,
    Long idUsuarioManutencao
) {}
