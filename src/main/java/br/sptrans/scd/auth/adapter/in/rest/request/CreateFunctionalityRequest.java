package br.sptrans.scd.auth.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFunctionalityRequest(
    @NotBlank String codSistema,
    @NotBlank String codModulo,
    @NotBlank String codRotina,
    @NotBlank String codFuncionalidade,
    @NotBlank String nomFuncionalidade,
    @NotNull Long idUsuarioManutencao
) {}
