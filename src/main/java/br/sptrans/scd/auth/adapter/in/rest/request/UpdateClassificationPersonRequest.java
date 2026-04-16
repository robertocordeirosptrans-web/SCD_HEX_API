package br.sptrans.scd.auth.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UpdateClassificationPersonRequest(
    @NotBlank @Size(max = 20) String codClassificacaoPessoa,
    @NotBlank @Size(max = 60) String desClassificacaoPessoa,
    @Size(max = 1) String flgVenda,
    @NotNull LocalDateTime dtManutencao,
    String stClassificacoesPessoa,
    @NotNull Long idUsuarioManutencao
) {}
