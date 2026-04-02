package br.sptrans.scd.auth.application.usecases.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.out.EmailSendingPort;
import br.sptrans.scd.auth.application.port.out.PasswordTokenPort;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;

import br.sptrans.scd.auth.domain.PasswordResetToken;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Solicitar Redefinição de Senha
 * 
 * Responsável por: - Validar se e-mail existe no sistema - Gerar token UUID
 * único - Invalidar token anterior se existir - Enviar e-mail de recuperação
 * 
 * Portos utilizados: - Output Port: userPort — buscar usuário por e-mail -
 * Output Port: PasswordTokenRepository — persistir token - Output Port:
 * EmailSendingPort — enviar e-mail
 */
@Component
@Transactional
@RequiredArgsConstructor
public class RequestPasswordResetUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestPasswordResetUseCase.class);

    @Value("${scd.auth.token-ttl-minutos:15}")
    private long tokenTtlMinutos;

    private final UserQueryPort userPort;
    private final PasswordTokenPort tokenRepository;
    private final EmailSendingPort emailPort;

    /**
     * Solicita redefinição de senha gerando um token e enviando e-mail.
     * 
     * Regras de negócio: - Verifica NOM_EMAIL na tabela USUARIOS - Gera token
     * UUID único; persiste com TTL - Invalida token anterior se existir -
     * Dispara e-mail SMTP apenas para e-mails cadastrados
     * 
     * @param command contém o e-mail solicitado
     * @throws ResourceNotFoundException se e-mail não encontrado
     */
    public void requestPasswordReset(AuthUseCase.ResetRequestComand command) {
        log.info("Solicitando reset de senha para e-mail: {}", command.email());
        
        // Busca usuário pelo e-mail
        User user = userPort.findByNomEmail(command.email())
            .orElseThrow(() -> {
                log.warn("E-mail não cadastrado: {}", command.email());
                return new ResourceNotFoundException("E-mail não cadastrado.");
            });

        // Invalida token anterior, se existir
        tokenRepository.invalidateTokensForUser(user.getIdUsuario());
        log.debug("Tokens anteriores invalidados para usuário: {}", user.getIdUsuario());

        // Cria novo token com TTL
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setIdUsuario(user.getIdUsuario());
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setDtExpiracao(LocalDateTime.now().plusMinutes(tokenTtlMinutos));

        tokenRepository.save(resetToken);
        log.info("Token de reset gerado para usuário: {}", user.getIdUsuario());

        // Envia e-mail via EmailSendingPort (novo padrão hexagonal)
        emailPort.sendPasswordResetEmail(
            user.getNomEmail(),
            user.getNomUsuario(),
            resetToken.getToken());
        
        log.info("E-mail de recuperação enviado para: {}", user.getNomEmail());
    }
}
