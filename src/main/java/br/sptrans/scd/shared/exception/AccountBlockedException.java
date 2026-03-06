package br.sptrans.scd.shared.exception;

/**
 * Exceção lançada quando a conta está bloqueada
 * Final class - não pode ser estendida
 */
public final class AccountBlockedException extends BusinessException {
    
    public AccountBlockedException(String message) {
        super(message, "ACCOUNT_BLOCKED");
    }

    public AccountBlockedException() {
        super("Conta bloqueada devido a múltiplas tentativas de login falhadas", "ACCOUNT_BLOCKED");
    }
}

