package br.sptrans.scd.shared.exception;

/**
 * Exceção lançada quando um campo possui formato inválido
 */
public class InvalidFormatException extends ValidationException {
    
    public InvalidFormatException(String fieldName, String expectedFormat) {
        super(String.format("Campo '%s' possui formato inválido. Formato esperado: %s", 
              fieldName, expectedFormat));
    }

    public InvalidFormatException(String message) {
        super(message);
    }
}

