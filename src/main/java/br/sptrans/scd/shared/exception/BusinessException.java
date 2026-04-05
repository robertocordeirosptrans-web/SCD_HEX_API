package br.sptrans.scd.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção base para erros de negócio usando Sealed Classes (Java 17+)
 * Sealed classes garantem que apenas subclasses conhecidas podem estender esta classe
 * Implementa ModuleException para padronização de tratamento no GlobalExceptionHandler
 */
public sealed class BusinessException extends RuntimeException implements ModuleException
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

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Determina o HttpStatus apropriado baseado no tipo de exceção
     * Mapeia cada tipo de subexceção ao seu respetivo código HTTP
     */
    @Override
    public HttpStatus getHttpStatus() {
        // Verifica o tipo real da exceção e retorna o status apropriado
        if (this instanceof ResourceNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (this instanceof DuplicateResourceException) {
            return HttpStatus.CONFLICT;
        } else if (this instanceof ValidationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (this instanceof AuthenticationFailedException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (this instanceof AccountBlockedException || 
                   this instanceof InactiveUserException ||
                   this instanceof InvalidUserProfileException) {
            return HttpStatus.FORBIDDEN;
        }
        // Default: 422 Unprocessable Entity para erros de negócio genéricos
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}