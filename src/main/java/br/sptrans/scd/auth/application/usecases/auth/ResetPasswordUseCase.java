package br.sptrans.scd.auth.application.usecases.auth;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.AuthUseCase;
import br.sptrans.scd.auth.application.port.in.PasswordValidator;
import br.sptrans.scd.auth.application.port.out.PasswordTokenPort;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;

import br.sptrans.scd.auth.application.port.out.UserStatusPort;
import br.sptrans.scd.auth.domain.PasswordResetToken;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.vo.PasswordValidationResult;
import br.sptrans.scd.shared.exception.AuthenticationFailedException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.exception.ValidationException;
import br.sptrans.scd.shared.security.PasswordHashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Redefinir Senha com Token
 * 
 * Responsável por: - Validar token (não expirado, não utilizado) - Validar
 * complexidade da nova senha - Impedir reutilização de senhas anteriores -
 * Atualizar senha no sistema - Invalidar tokens
 * 
 * Portos utilizados: - Output Port: PasswordTokenRepository — buscar/atualizar
 * token - Output Port: userQueryPort — buscar usuário - Output Port:
 * UserStatusRepository — atualizar senha - Input Port: PasswordValidator —
 * validar complexidade
 */
@Component
@Transactional
@RequiredArgsConstructor
public class ResetPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordUseCase.class);

    @Value("${scd.auth.senha-expira-dias:90}")
    private long senhaExpiraDias;

    private final PasswordTokenPort tokenRepository;
    private final UserQueryPort userQueryPort;
    private final UserStatusPort userStatusPort;
    private final PasswordValidator passwordValidator;

    /**
     * Redefinir senha com token válido.
     * 
     * Regras de negócio: - Valida token: não expirado, não utilizado - Compara
     * MD5 com OLD_SENHA para impedir reutilização - Regras de complexidade:
     * maiúscula + minúscula + especial + número + sem sequências - Marca token
     * como used=true após redefinição
     * 
     * @param command contém token e nova senha
     * @throws AuthenticationFailedException se token inválido/expirado
     * @throws ValidationException se senha não atende política
     */
    public void resetPassword(AuthUseCase.ResetPasswordComand command) {
        log.info("Iniciando reset de senha com token");
        
        // Busca token
        PasswordResetToken tokenObj = tokenRepository.findByToken(command.token())
                .orElseThrow(() -> {
                    log.warn("Token inválido ou não encontrado");
                    return new AuthenticationFailedException(
                        "Token inválido ou não encontrado.");
                });

        // Valida expiração
        if (tokenObj.isExpired()) {
            log.warn("Token expirado: {}", command.token());
            throw new AuthenticationFailedException(
                "Token expirado. Solicite um novo e-mail de recuperação.");
        }

        // Valida se já foi utilizado
        if (!tokenObj.isValid()) {
            log.warn("Token já utilizado: {}", command.token());
            throw new AuthenticationFailedException(
                "Token já utilizado. Solicite um novo e-mail de recuperação.");
        }

        // Busca usuário
        User user = userQueryPort.findById(tokenObj.getIdUsuario())
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado para token: {}", command.token());
                    return new ResourceNotFoundException("Usuário", "id", tokenObj.getIdUsuario());
                });

        // Valida complexidade e reutilização da nova senha
        PasswordValidationResult validacao = passwordValidator.validate(
            command.novaSenha(), 
            user.getCodSenha());
        
        if (!validacao.isValid()) {
            log.warn("Senha não atende política de complexidade: {}", validacao.getErrorsAsString());
            throw new ValidationException(validacao.getErrorsAsString());
        }

        // Persiste nova senha
        String newHash = PasswordHashUtil.hashBcrypt(command.novaSenha());
        String oldHash = user.getCodSenha();
        
        userStatusPort.updatePassword(
                user.getIdUsuario(), 
                newHash, 
                oldHash,
                LocalDateTime.now().plusDays(senhaExpiraDias));
        
        log.info("Senha atualizada para usuário: {}", user.getIdUsuario());

        // Marca token como utilizado e invalida todos os demais
        tokenObj.markAsUsed();
        tokenRepository.invalidateTokensForUser(user.getIdUsuario());
        
        log.info("Token marcado como utilizado");
    }
}
