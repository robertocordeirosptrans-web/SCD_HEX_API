package br.sptrans.scd.auth.application.port.in;

import br.sptrans.scd.auth.domain.vo.PasswordValidationResult;

/**
 * Porto de entrada: validação de complexidade de senha.
 * <p>
 * Implementações concretas definem a política aplicada
 * (ex.: {@code StrictPasswordValidator}, {@code ModeratePasswordValidator}).
 */
public interface PasswordValidator {

    /**
     * Valida a nova senha segundo a política da implementação.
     *
     * @param password        nova senha em texto simples
     * @param oldPasswordHash hash BCrypt da senha atual (pode ser {@code null})
     * @return resultado contendo {@code true} em caso de sucesso ou a lista de erros encontrados
     */
    PasswordValidationResult validate(String password, String oldPasswordHash);
}
