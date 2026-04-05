package br.sptrans.scd.auth.domain.vo;

import br.sptrans.scd.auth.domain.ClassificationPerson;
import lombok.Builder;
import lombok.Value;

/**
 * Value Object imutável: dados pessoais (PII) do usuário.
 */
@Value
@Builder(toBuilder = true)
public class PersonalInfo {

    String nomUsuario;
    String nomEmail;
    String codCpf;
    String codRg;
    Long numTelefone;
    String desEndereco;
    String nomDepartamento;
    String nomCargo;
    String nomFuncao;
    String codEmpresa;
    ClassificationPerson codClassificacaoPessoa;
}
