package br.sptrans.scd.auth.adapter.port.in.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
    @Size(max = 60, message = "Nome do usuário deve ter no máximo 60 caracteres")
    String nomUsuario,

    @Size(max = 80, message = "Endereço deve ter no máximo 80 caracteres")
    String desEndereco,

    @Size(max = 20, message = "Departamento deve ter no máximo 20 caracteres")
    String nomDepartamento,

    @Size(max = 20, message = "Cargo deve ter no máximo 20 caracteres")
    String nomCargo,

    @Size(max = 20, message = "CPF deve ter no máximo 20 caracteres")
    String codCpf,

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    String codRg,

    @Size(max = 20, message = "Função deve ter no máximo 20 caracteres")
    String nomFuncao,

    Long numTelefone,

    @Email(message = "Email deve ser válido")
    @Size(max = 40, message = "Email deve ter no máximo 40 caracteres")
    String nomEmail,

    @Size(max = 20, message = "Código da empresa deve ter no máximo 20 caracteres")
    String codEmpresa,

    String codClassificacaoPessoa,

    LocalDateTime dt_jornada_ini,

    LocalDateTime dt_jornada_fim
) {
}