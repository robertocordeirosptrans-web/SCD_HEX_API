package br.sptrans.scd.auth.domain.vo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Value;

/**
 * Value Object imutável que representa o resultado de uma validação de senha.
 * Retornado por {@link br.sptrans.scd.auth.application.port.in.PasswordValidator}.
 */
@Value
public class PasswordValidationResult {

    boolean valid;
    List<String> errors;

    public static PasswordValidationResult success() {
        return new PasswordValidationResult(true, Collections.emptyList());
    }

    public static PasswordValidationResult failure(String... errors) {
        return new PasswordValidationResult(false, Arrays.asList(errors));
    }

    public static PasswordValidationResult failure(List<String> errors) {
        return new PasswordValidationResult(false, Collections.unmodifiableList(errors));
    }

    public String getErrorsAsString() {
        return String.join("; ", errors);
    }
}
