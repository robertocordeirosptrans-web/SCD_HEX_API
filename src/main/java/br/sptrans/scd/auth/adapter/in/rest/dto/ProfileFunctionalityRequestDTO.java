package br.sptrans.scd.auth.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProfileFunctionalityRequestDTO(

        @NotBlank(message = "Código do sistema é obrigatório")
        @Size(max = 10, message = "Código do sistema deve ter no máximo 10 caracteres")
        String codSistema,

        @NotBlank(message = "Código do módulo é obrigatório")
        @Size(max = 10, message = "Código do módulo deve ter no máximo 10 caracteres")
        String codModulo,

        @NotBlank(message = "Código da rotina é obrigatório")
        @Size(max = 10, message = "Código da rotina deve ter no máximo 10 caracteres")
        String codRotina,

        @NotBlank(message = "Código da funcionalidade é obrigatório")
        @Size(max = 30, message = "Código da funcionalidade deve ter no máximo 30 caracteres")
        String codFuncionalidade,

        @NotBlank(message = "Código do perfil é obrigatório")
        @Size(max = 20, message = "Código do perfil deve ter no máximo 20 caracteres")
        String codPerfil,

        @NotNull(message = "ID do usuário logado é obrigatório")
        @Positive(message = "ID do usuário logado deve ser positivo")
        Long idUsuarioLogado
) {
}
