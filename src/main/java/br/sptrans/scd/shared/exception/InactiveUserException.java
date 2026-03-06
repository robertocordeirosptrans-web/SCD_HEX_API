package br.sptrans.scd.shared.exception;

/**
 * Exceção lançada quando o usuário está inativo
 * Final class - não pode ser estendida
 */
public final class InactiveUserException extends BusinessException {
    
    public InactiveUserException(String message) {
        super(message, "USER_INACTIVE");
    }

    public InactiveUserException() {
        super("Usuário inativo", "USER_INACTIVE");
    }
}

