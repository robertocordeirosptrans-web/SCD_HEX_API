package br.sptrans.scd.auth.application.port.out;

/**
 * Porta de Saída — gateway de envio de e-mail. O adaptador SMTP fica fora do
 * domínio; a interface garante testabilidade.
 */
public interface GatewayEmail {
    /**
     * Envia e-mail de redefinição de senha com o token gerado.
     *
     * @param destinatario e-mail do usuário
     * @param nomeUsuario nome para saudação
     * @param token token UUID único para montar o link de redefinição
     */

    void sendPasswordResetEmail(String destinatario, String nomeUsuario, String token);
}
