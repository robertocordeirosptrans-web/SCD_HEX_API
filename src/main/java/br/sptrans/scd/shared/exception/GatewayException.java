package br.sptrans.scd.shared.exception;

/**
 * Exceção sealed para erros de integração com gateways externos.
 * Sealed classes garantem que apenas subclasses conhecidas podem estender esta classe.
 * Esta exceção é lançada quando falhas ocorrem em adapters de saída (SMTP, JWT, etc.)
 */
public sealed class GatewayException extends RuntimeException
    permits EmailGatewayException,
            TokenGatewayException {

    private final String errorCode;
    private final String gatewayName;

    public GatewayException(String message, String gatewayName) {
        super(message);
        this.gatewayName = gatewayName;
        this.errorCode = "GATEWAY_ERROR";
    }

    public GatewayException(String message, String gatewayName, String errorCode) {
        super(message);
        this.gatewayName = gatewayName;
        this.errorCode = errorCode;
    }

    public GatewayException(String message, String gatewayName, Throwable cause) {
        super(message, cause);
        this.gatewayName = gatewayName;
        this.errorCode = "GATEWAY_ERROR";
    }

    public GatewayException(String message, String gatewayName, String errorCode, Throwable cause) {
        super(message, cause);
        this.gatewayName = gatewayName;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getGatewayName() {
        return gatewayName;
    }
}
