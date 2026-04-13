package br.sptrans.scd.auth.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UserProfileRequestDTO(

        @NotNull(message = "ID do usuário é obrigatório")
        @Positive(message = "ID do usuário deve ser positivo")
        Long idUsuario,

        @NotBlank(message = "Código do perfil é obrigatório")
        @Size(max = 20, message = "Código do perfil deve ter no máximo 20 caracteres")
        String codPerfil
) {
}
