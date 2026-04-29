package br.sptrans.scd.auth.application.usecases.auth;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.PasswordValidator;
import br.sptrans.scd.auth.application.port.in.SessionManagementUseCase;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;
import br.sptrans.scd.auth.application.port.out.UserStatusPort;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.vo.PasswordValidationResult;
import br.sptrans.scd.shared.exception.AuthenticationFailedException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.exception.ValidationException;
import br.sptrans.scd.shared.security.PasswordHashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Trocar Senha (Usuário Autenticado)
 * 
 * Responsável por: - Validar token JWT do header Authorization - Buscar usuário autenticado - Validar 
 * senha atual fornecida - Validar complexidade da nova senha - Impedir reutilização de senhas -
 * Atualizar senha mantendo hash anterior - Invalidar todas as sessões do usuário (logout em todos os
 * dispositivos) - Mover hash anterior para coluna oldSenha - Atualizar data de expiração (+3 meses)
 * 
 * Portos utilizados: - Output Port: UserQueryPort — buscar usuário por ID - Output Port:
 * UserStatusPort — atualizar senha - Output Port: PasswordValidator — validar complexidade - Output
 * Port: SessionManagementUseCase — invalidar todas as sessões do usuário
 * 
 * Regras de segurança: - Senha atual OBRIGATÓRIA no corpo da requisição - Comparação com PasswordHashUtil
 * para detectar tipo de hash - Nova senha gerada com hash BCrypt (padrão forte) - Invalidação de tokens
 * para forçar novo login
 */
@Component
@Transactional
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordUseCase.class);

    @Value("${scd.auth.senha-expira-dias:90}")
    private long senhaExpiraDias;

    private final UserQueryPort userQueryPort;
    private final UserStatusPort userStatusPort;
    private final PasswordValidator passwordValidator;
    private final SessionManagementUseCase sessionManagementUseCase;

    /**
     * Trocar senha do usuário autenticado.
     * 
     * Passos: 1. Valida se usuário existe 2. Compara senha atual com hash armazenado
     * em codSenha (detecta tipo de hash automaticamente) 3. Valida complexidade da
     * nova senha 4. Impede reutilização comparando com oldSenha 5. Gera novo hash
     * BCrypt para a nova senha 6. Move hash anterior para oldSenha 7. Atualiza
     * dtModi com timestamp atual 8. Atualiza dtExpiraSenha com +3 meses 9. Invalida
     * todas as sessões do usuário (logout em todos os dispositivos)
     * 
     * @param idUsuario ID do usuário autenticado (extraído do token JWT)
     * @param senhaAtual senha atual em texto plano fornecida pelo usuário
     * @param novaSenha nova senha em texto plano (será hashada com BCrypt)
     * @throws ResourceNotFoundException se usuário não encontrado
     * @throws AuthenticationFailedException se senha atual incorreta
     * @throws ValidationException se nova senha não atende política de complexidade
     */
    public void changePassword(Long idUsuario, String senhaAtual, String novaSenha) {
        log.info("Iniciando troca de senha para usuário ID: {}", idUsuario);

        // 1. Busca usuário
        User user = userQueryPort.findById(idUsuario)
            .orElseThrow(() -> {
                log.warn("Usuário não encontrado. ID: {}", idUsuario);
                return new ResourceNotFoundException("Usuário", "id", idUsuario);
            });

        // 2. Valida senha atual (compara com hash armazenado)
        // O PasswordHashUtil detecta automaticamente o tipo de hash
        if (!PasswordHashUtil.verificar(senhaAtual, user.getCodSenha())) {
            log.warn("Senha atual incorreta para usuário ID: {}", idUsuario);
            throw new AuthenticationFailedException("Senha atual incorreta.");
        }

        // 3. Valida complexidade e reutilização da nova senha
        PasswordValidationResult validacao = passwordValidator.validate(
            novaSenha,
            user.getCodSenha());

        if (!validacao.isValid()) {
            log.warn("Nova senha não atende política. Usuário ID: {}. Erros: {}", 
                idUsuario, validacao.getErrorsAsString());
            throw new ValidationException(validacao.getErrorsAsString());
        }

        // 4. Detecta tipo de hash da senha anterior para logging
        PasswordHashUtil.TipoHash tipoHashAnterior = PasswordHashUtil.detectarTipoHash(user.getCodSenha());
        
        // 5. Gera novo hash (usa BCrypt como padrão forte)
        String novoHash = PasswordHashUtil.hashBcrypt(novaSenha);
        String hashAnterior = user.getCodSenha();

        // 6. Atualiza senha (move anterior para oldSenha, atualiza dtExpiraSenha)
        LocalDateTime novaDataExpiracao = LocalDateTime.now().plusDays(senhaExpiraDias);
        userStatusPort.updatePassword(
            idUsuario,
            novoHash,
            hashAnterior,
            novaDataExpiracao);

        log.info("Senha atualizada para usuário ID: {}. Hash anterior tipo: {}, novo tipo: BCrypt, expiração: +{} dias",
            idUsuario, tipoHashAnterior, senhaExpiraDias);

        // 7. Invalida todas as sessões do usuário (logout em todos os dispositivos)
        // Motivo: PASSWORD_CHANGED força novo login com nova senha
        sessionManagementUseCase.revokeAllUserSessions(idUsuario, "PASSWORD_CHANGED");
        log.info("Todas as sessões revogadas para usuário ID: {}. Motivo: PASSWORD_CHANGED", idUsuario);
    }
}
