package br.sptrans.scd.auth.adapter.in.rest.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "Login é obrigatório") @Size(min = 3, max = 10, message = "Login deve ter entre 3 e 10 caracteres") String codLogin,

        @NotBlank(message = "Senha é obrigatória") @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String codSenha,

        @NotBlank(message = "Nome do usuário é obrigatório") @Size(max = 60, message = "Nome do usuário deve ter no máximo 60 caracteres") String nomUsuario,

        @NotBlank(message = "Endereço é obrigatório") @Size(max = 80, message = "Endereço deve ter no máximo 80 caracteres") String desEndereco,

        @Size(max = 20, message = "Departamento deve ter no máximo 20 caracteres") String nomDepartamento,

        @Size(max = 20, message = "Cargo deve ter no máximo 20 caracteres") String nomCargo,

        @Size(max = 20, message = "Função deve ter no máximo 20 caracteres") String nomFuncao,

        @NotBlank(message = "CPF é obrigatório") @Size(max = 20, message = "CPF deve ter no máximo 20 caracteres") String codCpf,

        @NotBlank(message = "RG é obrigatório") @Size(max = 20, message = "RG deve ter no máximo 20 caracteres") String codRg,

        @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ser válido") @Size(max = 40, message = "Email deve ter no máximo 40 caracteres") String nomEmail,

        @NotBlank(message = "Classificação da pessoa é obrigatória") String codClassificacaoPessoa,

        @NotBlank(message = "Código da empresa é obrigatório") @Size(max = 20, message = "Código da empresa deve ter no máximo 20 caracteres") String codEmpresa,
        String dtJornadaIni,
        String dtJornadaFim,
        String numDiasSemanasPermitidos
    ) {
}
