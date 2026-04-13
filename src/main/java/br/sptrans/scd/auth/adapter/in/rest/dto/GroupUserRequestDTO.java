package br.sptrans.scd.auth.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record GroupUserRequestDTO(

        @NotBlank(message = "Código do grupo é obrigatório")
        @Size(max = 20, message = "Código do grupo deve ter no máximo 20 caracteres")
        String codGrupo,

        @NotNull(message = "ID do usuário é obrigatório")
        @Positive(message = "ID do usuário deve ser positivo")
        Long idUsuario
) {
}
