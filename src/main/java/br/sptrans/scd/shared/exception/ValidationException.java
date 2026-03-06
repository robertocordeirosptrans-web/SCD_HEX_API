package br.sptrans.scd.shared.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exceção lançada quando há erros de validação
 * Non-sealed permite que outras classes estendam esta exceção
 */
public non-sealed class ValidationException extends BusinessException {
    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.errors = new HashMap<>();
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message, "VALIDATION_ERROR");
        this.errors = errors;
    }

    public ValidationException(Map<String, String> errors) {
        super("Erro de validação", "VALIDATION_ERROR");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void addError(String field, String message) {
        this.errors.put(field, message);
    }
}
