package br.sptrans.scd.auth.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileFunctionalityRequestDTO(

        @NotBlank(message = "Código do sistema é obrigatório")
        @Size(max = 20, message = "Código do sistema deve ter no máximo 20 caracteres")
        String codSistema,

        @NotBlank(message = "Código do módulo é obrigatório")
        @Size(max = 20, message = "Código do módulo deve ter no máximo 20 caracteres")
        String codModulo,

        @NotBlank(message = "Código da rotina é obrigatório")
        @Size(max = 20, message = "Código da rotina deve ter no máximo 20 caracteres")
        String codRotina,

        @NotBlank(message = "Código da funcionalidade é obrigatório")
        @Size(max = 20, message = "Código da funcionalidade deve ter no máximo 20 caracteres")
        String codFuncionalidade,

        @NotBlank(message = "Código do perfil é obrigatório")
        @Size(max = 20, message = "Código do perfil deve ter no máximo 20 caracteres")
        String codPerfil
) {
}
