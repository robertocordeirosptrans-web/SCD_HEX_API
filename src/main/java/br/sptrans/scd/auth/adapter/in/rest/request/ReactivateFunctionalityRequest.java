package br.sptrans.scd.auth.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReactivateFunctionalityRequest(
    @NotBlank String codSistema,
    @NotBlank String codModulo,
    @NotBlank String codRotina,
    @NotBlank String codFuncionalidade,
    @NotNull Long idUsuarioManutencao
) {}
