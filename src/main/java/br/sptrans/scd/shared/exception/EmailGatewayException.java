package br.sptrans.scd.shared.exception;

/**
 * Exceção final para erros de integração com o gateway de e-mail SMTP.
 * Lançada quando ocorrem falhas no envio de e-mails via adapter SMTP.
 * Exemplos: falha na conexão, erro de autenticação, template inválido, etc.
 */
public final class EmailGatewayException extends GatewayException {

    public EmailGatewayException(String message) {
        super(message, "SMTP_GATEWAY");
    }

    public EmailGatewayException(String message, String errorCode) {
        super(message, "SMTP_GATEWAY", errorCode);
    }

    public EmailGatewayException(String message, Throwable cause) {
        super(message, "SMTP_GATEWAY", cause);
    }

    public EmailGatewayException(String message, String errorCode, Throwable cause) {
        super(message, "SMTP_GATEWAY", errorCode, cause);
    }
}
