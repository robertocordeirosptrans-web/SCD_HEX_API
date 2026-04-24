package br.sptrans.scd.auth.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequestDTO(

        @NotBlank(message = "Nome do perfil é obrigatório")
        @Size(max = 100, message = "Nome do perfil deve ter no máximo 100 caracteres")
        String nomPerfil
) {
}
