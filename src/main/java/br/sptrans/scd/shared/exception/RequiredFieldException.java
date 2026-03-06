package br.sptrans.scd.shared.exception;

/**
 * Exceção lançada quando um campo obrigatório está ausente
 */
public class RequiredFieldException extends ValidationException {
    
    public RequiredFieldException(String fieldName) {
        super(String.format("Campo obrigatório '%s' não pode ser nulo ou vazio", fieldName));
    }

    public RequiredFieldException(String message, String fieldName) {
        super(message);
        addError(fieldName, message);
    }
}

