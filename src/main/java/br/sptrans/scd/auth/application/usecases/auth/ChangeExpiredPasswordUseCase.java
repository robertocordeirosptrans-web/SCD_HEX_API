package br.sptrans.scd.auth.application.usecases.auth;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.PasswordValidator;
import br.sptrans.scd.auth.application.port.in.SessionManagementUseCase;
import br.sptrans.scd.auth.application.port.out.PasswordTokenPort;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;
import br.sptrans.scd.auth.application.port.out.UserStatusPort;
import br.sptrans.scd.auth.domain.PasswordResetToken;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.vo.PasswordValidationResult;
import br.sptrans.scd.shared.exception.AuthenticationFailedException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.exception.ValidationException;
import br.sptrans.scd.shared.security.Criptografia;
import br.sptrans.scd.shared.security.PasswordHashUtil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChangeExpiredPasswordUseCase {
    private static final Logger log = LoggerFactory.getLogger(ChangeExpiredPasswordUseCase.class);

    private final PasswordTokenPort tokenRepository;
    private final UserQueryPort userQueryPort;
    private final UserStatusPort userStatusPort;
    private final PasswordValidator passwordValidator;
    private final SessionManagementUseCase sessionManagementUseCase;

    public User changeExpiredPassword(String resetToken, String userId, String currentPassword, String newPassword) {
        // 1. Busca e valida o token
        PasswordResetToken tokenObj = tokenRepository.findByToken(resetToken)
                .orElseThrow(() -> new AuthenticationFailedException("Token inválido ou não encontrado."));
        if (tokenObj.isExpired()) {
            throw new AuthenticationFailedException("Token expirado. Solicite um novo e-mail de recuperação.");
        }
        if (!tokenObj.isValid()) {
            throw new AuthenticationFailedException("Token já utilizado. Solicite um novo e-mail de recuperação.");
        }
        if (!tokenObj.getIdUsuario().toString().equals(userId)) {
            throw new AuthenticationFailedException("Token não corresponde ao usuário informado.");
        }

        // 2. Busca usuário
        User user = userQueryPort.findById(tokenObj.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", tokenObj.getIdUsuario()));

        // 3. Verifica se current_password confere com a senha expirada
        if (!PasswordHashUtil.verificar(currentPassword, user.getCodSenha())) {
            throw new AuthenticationFailedException("Senha atual incorreta.");
        }

        // 4. Aplica política de nova senha
        PasswordValidationResult validacao = passwordValidator.validate(newPassword, user.getCodSenha());
        if (!validacao.isValid()) {
            throw new ValidationException(validacao.getErrorsAsString());
        }

        // 4. Detecta tipo de hash da senha anterior para logging e para gerar o novo hash
        PasswordHashUtil.TipoHash tipoHashAnterior = PasswordHashUtil.detectarTipoHash(user.getCodSenha());

        // 5. Gera novo hash usando o mesmo tipo do hash anterior
        String novoHash;
        novoHash = switch (tipoHashAnterior) {
            case BCRYPT -> PasswordHashUtil.hashBcrypt(newPassword);
            case MD5 -> Criptografia.getMD5Hex(newPassword);
            case SHA256 -> PasswordHashUtil.hashSha256(newPassword);
            default -> PasswordHashUtil.hashBcrypt(newPassword);
        }; // fallback para BCrypt se tipo desconhecido
        String hashAnterior = user.getCodSenha();
   

        userStatusPort.updatePassword(user.getIdUsuario(), novoHash, hashAnterior, LocalDateTime.now().plusDays(90));

        // 6. Marca token como utilizado e invalida demais tokens
        tokenObj.markAsUsed();
        tokenRepository.invalidateTokensForUser(user.getIdUsuario());

        // 7. Invalida todas as sessões do usuário
        sessionManagementUseCase.revokeAllUserSessions(user.getIdUsuario(), "PASSWORD_EXPIRED_CHANGE");

        return user;
    }
}
