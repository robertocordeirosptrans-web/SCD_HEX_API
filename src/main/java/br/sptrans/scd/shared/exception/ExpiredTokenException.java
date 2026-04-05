package br.sptrans.scd.shared.exception;

/**
 * Exceção final para token expirado.
 * Lançada quando um token JWT válido mas expirado é utilizado.
 */
public final class ExpiredTokenException extends TokenException {

    public ExpiredTokenException(String message) {
        super(message, "EXPIRED_TOKEN");
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, "EXPIRED_TOKEN", cause);
    }
}
