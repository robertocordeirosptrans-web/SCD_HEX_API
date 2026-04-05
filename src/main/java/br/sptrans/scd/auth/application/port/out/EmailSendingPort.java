package br.sptrans.scd.auth.application.port.out;

/**
 * Porta de Saída — Serviço de Envio de E-mail (responsabilidade: COMUNICAÇÃO EXTERNA).
 * <p>Implementação pode ser SMTP, SendGrid, AWS SES ou qualquer provedor.
 * Esta porta substitui o padrão `GatewayEmail` por nomenclatura consistente com
 * a arquitetura hexagonal: todos os portos devem terminar em "Port".</p>
 */
public interface EmailSendingPort {

    /**
     * Envia e-mail de redefinição de senha.
     *
     * @param destinatario e-mail do usuário
     * @param nomeUsuario nome para saudação
     * @param token token UUID único para montar o link de redefinição
     */
    void sendPasswordResetEmail(String destinatario, String nomeUsuario, String token);
}
