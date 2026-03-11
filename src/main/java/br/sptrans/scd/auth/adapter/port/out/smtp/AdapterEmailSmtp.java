package br.sptrans.scd.auth.adapter.port.out.smtp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import br.sptrans.scd.auth.application.port.out.GatewayEmail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Adaptador de Saída SMTP — implementa GatewayEmail.
 * Usa JavaMailSender do Spring para envio via SMTP.
 * Conforme US001: envia e-mail somente para endereços cadastrados na base.
 */
@Component
public class AdapterEmailSmtp implements GatewayEmail {



    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.url}")
    private String url;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public AdapterEmailSmtp(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @SuppressWarnings("null")
    @Override
    public void sendPasswordResetEmail(String destinatario, String nomeUsuario, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("Recuperação de Senha - Controle de Acesso - SCD");

            String resetLink = url + "/reset-password?token=" + token;
            Context context = new Context();
            context.setVariable("resetLink", resetLink);
            context.setVariable("expirationMinutes", 10);
            context.setVariable("nomeUsuario", nomeUsuario);

            String htmlContent = templateEngine.process("password-reset-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}
