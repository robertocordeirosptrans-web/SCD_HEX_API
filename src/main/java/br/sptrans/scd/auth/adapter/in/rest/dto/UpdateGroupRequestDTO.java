package br.sptrans.scd.auth.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateGroupRequestDTO(

        @NotBlank(message = "Nome do grupo é obrigatório")
        @Size(max = 100, message = "Nome do grupo deve ter no máximo 100 caracteres")
        String nomGrupo,

        @NotNull(message = "ID do usuário logado é obrigatório")
        @Positive(message = "ID do usuário logado deve ser positivo")
        Long idUsuarioLogado
) {
}
