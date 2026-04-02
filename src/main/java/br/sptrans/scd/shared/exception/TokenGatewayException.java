package br.sptrans.scd.shared.exception;

/**
 * Exceção final para erros de integração com o gateway de tokens JWT.
 * Lançada quando ocorrem falhas na geração ou validação de tokens.
 * Exemplos: secret inválido, algoritmo não suportado, erro de assinatura, etc.
 */
public final class TokenGatewayException extends GatewayException {

    public TokenGatewayException(String message) {
        super(message, "JWT_GATEWAY");
    }

    public TokenGatewayException(String message, String errorCode) {
        super(message, "JWT_GATEWAY", errorCode);
    }

    public TokenGatewayException(String message, Throwable cause) {
        super(message, "JWT_GATEWAY", cause);
    }

    public TokenGatewayException(String message, String errorCode, Throwable cause) {
        super(message, "JWT_GATEWAY", errorCode, cause);
    }
}
