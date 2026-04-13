package br.sptrans.scd.auth.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateProfileRequestDTO(

        @NotBlank(message = "Código do perfil é obrigatório")
        @Size(max = 20, message = "Código do perfil deve ter no máximo 20 caracteres")
        String codPerfil,

        @NotBlank(message = "Nome do perfil é obrigatório")
        @Size(max = 100, message = "Nome do perfil deve ter no máximo 100 caracteres")
        String nomPerfil,

        @NotNull(message = "ID do usuário logado é obrigatório")
        @Positive(message = "ID do usuário logado deve ser positivo")
        Long idUsuarioLogado
) {
}
