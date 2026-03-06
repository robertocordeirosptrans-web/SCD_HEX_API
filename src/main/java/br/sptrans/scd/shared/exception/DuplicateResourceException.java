package br.sptrans.scd.shared.exception;

/**
 * Exceção lançada quando há tentativa de criar um recurso duplicado
 * Final class - não pode ser estendida
 */
public final class DuplicateResourceException extends BusinessException {
    
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s já existe com %s: '%s'", resourceName, fieldName, fieldValue), 
              "DUPLICATE_RESOURCE");
    }

    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE");
    }
}

