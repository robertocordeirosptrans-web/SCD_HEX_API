package br.sptrans.scd.channel.domain.exception;

import br.sptrans.scd.channel.domain.enums.ChannelErrorType;

public class ChannelException extends RuntimeException {

    private final ChannelErrorType errorType;

    public ChannelException(ChannelErrorType errorType) {
        super(errorType.getDescription());
        this.errorType = errorType;
    }

    public ChannelException(ChannelErrorType errorType, Throwable cause) {
        super(errorType.getDescription(), cause);
        this.errorType = errorType;
    }

    public ChannelErrorType getErrorType() {
        return errorType;
    }
}
