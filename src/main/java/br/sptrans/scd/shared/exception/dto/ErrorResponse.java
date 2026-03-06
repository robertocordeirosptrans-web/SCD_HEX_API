package br.sptrans.scd.shared.exception.dto;


import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO para respostas de erro padronizadas
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String errorCode,
    String path,
    Map<String, String> validationErrors,
    Map<String, Object> details
) {
    public ErrorResponse(int status, String error, String message, String errorCode, String path) {
        this(LocalDateTime.now(), status, error, message, errorCode, path, null, null);
    }

    public ErrorResponse(int status, String error, String message, String errorCode, String path, Map<String, String> validationErrors) {
        this(LocalDateTime.now(), status, error, message, errorCode, path, validationErrors, null);
    }

    /**
     * Factory method for creating error responses with additional details.
     * Use this method when you need to include arbitrary metadata in the response
     * (e.g., flags, actions, or custom fields) but don't have validation errors.
     * 
     * @param status HTTP status code
     * @param error HTTP status description (e.g., "Forbidden", "Unauthorized")
     * @param message Human-readable error message
     * @param errorCode Application-specific error code
     * @param path Request path where the error occurred
     * @param details Additional metadata as key-value pairs
     * @return ErrorResponse with details field populated
     */
    public static ErrorResponse withDetails(int status, String error, String message, String errorCode, String path, Map<String, Object> details) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, errorCode, path, null, details);
    }
}

