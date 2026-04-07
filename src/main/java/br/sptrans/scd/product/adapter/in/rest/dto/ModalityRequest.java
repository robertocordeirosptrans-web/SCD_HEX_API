package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModalityRequest(
    @NotBlank @Size(max = 20) String codModalidade,
    @Size(max = 60) String desModalidade,
    String codStatus,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    Long idUsuarioCadastro,
    Long idUsuarioManutencao
) {}
