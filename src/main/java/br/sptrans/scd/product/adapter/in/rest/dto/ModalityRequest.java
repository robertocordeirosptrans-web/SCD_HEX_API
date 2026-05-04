package br.sptrans.scd.product.adapter.in.rest.dto;

import java.time.LocalDateTime;


import jakarta.validation.constraints.Size;

public record ModalityRequest(
    @Size(max = 60) String desModalidade,
    String codStatus,
    LocalDateTime dtCadastro,
    LocalDateTime dtManutencao,
    Long idUsuarioCadastro,
    Long idUsuarioManutencao
) {}
