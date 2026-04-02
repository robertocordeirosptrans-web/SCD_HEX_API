package br.sptrans.scd.shared.exception;

/**
 * Exceção final para token inválido.
 * Lançada quando a validação de um token JWT falha por razões de integridade ou formato.
 */
public final class InvalidTokenException extends TokenException {

    public InvalidTokenException(String message) {
        super(message, "INVALID_TOKEN");
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, "INVALID_TOKEN", cause);
    }
}
