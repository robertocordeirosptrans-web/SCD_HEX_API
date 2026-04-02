package br.sptrans.scd.shared.exception;

/**
 * Exceção base para erros de negócio usando Sealed Classes (Java 17+)
 * Sealed classes garantem que apenas subclasses conhecidas podem estender esta classe
 */
public sealed class BusinessException extends RuntimeException
    permits ValidationException,
            ResourceNotFoundException,
            DuplicateResourceException,
            AuthenticationFailedException,
            AccountBlockedException,
            InactiveUserException,
            InvalidUserProfileException {

    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}