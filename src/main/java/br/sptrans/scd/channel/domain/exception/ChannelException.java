package br.sptrans.scd.channel.domain.exception;

import org.springframework.http.HttpStatus;

import br.sptrans.scd.channel.domain.enums.ChannelErrorType;
import br.sptrans.scd.shared.exception.ModuleException;

public class ChannelException extends RuntimeException implements ModuleException {

    private final ChannelErrorType errorType;

    public ChannelException(ChannelErrorType errorType) {
        super(errorType.getDescription());
        this.errorType = errorType;
    }

    public ChannelException(ChannelErrorType errorType, String message) {
        super(message != null ? message : errorType.getDescription());
        this.errorType = errorType;
    }

    public ChannelException(ChannelErrorType errorType, Throwable cause) {
        super(errorType.getDescription(), cause);
        this.errorType = errorType;
    }

    public ChannelErrorType getErrorType() {
        return errorType;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return errorType.getHttpStatus();
    }

    @Override
    public String getErrorCode() {
        String name = errorType.name();
        if (name.contains("NOT_FOUND")) {
            return "CHANNEL_NOT_FOUND";
        } else if (name.contains("ALREADY_EXISTS") || name.contains("CODE_ALREADY_EXISTS")) {
            return "CHANNEL_DUPLICATE";
        } else if (name.contains("ALREADY_ACTIVE") || name.contains("ALREADY_INACTIVE")) {
            return "CHANNEL_INVALID_STATE";
        }
        return "CHANNEL_ERROR";
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
