package br.sptrans.scd.shared.exception;

/**
 * Exceção sealed para erros relacionados a tokens JWT.
 * Sealed classes garantem que apenas subclasses conhecidas podem estender esta classe.
 * Esta exceção é lançada quando ocorrem problemas na geração, validação ou processamento de tokens.
 */
public sealed class TokenException extends RuntimeException
    permits InvalidTokenException,
            ExpiredTokenException,
            TokenGenerationException {

    private final String errorCode;

    public TokenException(String message) {
        super(message);
        this.errorCode = "TOKEN_ERROR";
    }

    public TokenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "TOKEN_ERROR";
    }

    public TokenException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
