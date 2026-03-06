package br.sptrans.scd.shared.exception;

/**
 * Exceção lançada quando há falha na autenticação
 * Final class - não pode ser estendida
 */
public final class AuthenticationFailedException extends BusinessException {
    
    public AuthenticationFailedException(String message) {
        super(message, "AUTHENTICATION_FAILED");
    }

    public AuthenticationFailedException() {
        super("Credenciais inválidas", "AUTHENTICATION_FAILED");
    }
}

