package br.sptrans.scd.auth.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GroupProfileRequestDTO(

        @NotBlank(message = "Código do grupo é obrigatório")
        @Size(max = 20, message = "Código do grupo deve ter no máximo 20 caracteres")
        String codGrupo,

        @NotBlank(message = "Código do perfil é obrigatório")
        @Size(max = 20, message = "Código do perfil deve ter no máximo 20 caracteres")
        String codPerfil,

        @Pattern(regexp = "[AI]", message = "Status deve ser A (Ativo) ou I (Inativo)")
        String codStatus
) {
}
