package br.sptrans.scd.auth.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.PasswordValidator;
import br.sptrans.scd.auth.domain.vo.PasswordPolicy;
import br.sptrans.scd.auth.domain.vo.PasswordValidationResult;

/**
 * Validador moderado de senha.
 * <p>
 * Aplica apenas as regras básicas da {@link PasswordPolicy#moderate()}:
 * comprimento mínimo de 6 caracteres e não-branco.
 * Indicado para ambientes de desenvolvimento e testes.
 */
@Component("moderatePasswordValidator")
public class ModeratePasswordValidator implements PasswordValidator {

    private final PasswordPolicy policy;

    public ModeratePasswordValidator() {
        this.policy = PasswordPolicy.moderate();
    }

    @Override
    public PasswordValidationResult validate(String password, String oldPasswordHash) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isBlank()) {
            errors.add("A senha não pode ser vazia.");
        } else if (password.length() < policy.getMinLength()) {
            errors.add("A senha deve ter no mínimo " + policy.getMinLength() + " caracteres.");
        }

        return errors.isEmpty()
                ? PasswordValidationResult.success()
                : PasswordValidationResult.failure(errors);
    }
}
