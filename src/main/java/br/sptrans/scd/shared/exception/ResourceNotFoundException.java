package br.sptrans.scd.shared.exception;

/**
 * Exceção lançada quando um recurso não é encontrado
 * Final class - não pode ser estendida
 */
public final class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s não encontrado com %s: '%s'", resourceName, fieldName, fieldValue), 
              "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
}
