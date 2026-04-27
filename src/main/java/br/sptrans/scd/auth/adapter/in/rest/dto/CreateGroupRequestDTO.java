package br.sptrans.scd.auth.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGroupRequestDTO(

        @NotBlank(message = "Código do grupo é obrigatório")
        @Size(max = 20, message = "Código do grupo deve ter no máximo 20 caracteres")
        String codGrupo,

        @NotBlank(message = "Nome do grupo é obrigatório")
        @Size(max = 100, message = "Nome do grupo deve ter no máximo 100 caracteres")
        String nomGrupo
) {
}
