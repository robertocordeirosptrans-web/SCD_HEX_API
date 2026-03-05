package br.sptrans.scd.auth.adapter.out.smtp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class AdapterEmailSmtp {



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

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Recuperação de Senha - Controle de Acesso - SCD");

            String resetLink = url + "/reset-password?token=" + token;
            Context context = new Context();
            context.setVariable("resetLink", resetLink);
            context.setVariable("expirationMinutes", 10);

            String htmlContent = templateEngine.process("password-reset-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}
