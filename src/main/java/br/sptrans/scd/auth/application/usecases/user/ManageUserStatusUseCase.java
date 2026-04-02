package br.sptrans.scd.auth.application.usecases.user;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.out.AuthenticationPort;

import br.sptrans.scd.auth.application.port.out.UserQueryPort;

import br.sptrans.scd.auth.application.port.out.UserStatusPort;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.shared.cache.InvalidateUserCache;
import br.sptrans.scd.shared.exception.BusinessException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.security.PasswordHashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Gerenciar Status de Usuário
 * 
 * Responsável por: - Ativar/desativar usuários - Desbloquear usuários bloqueados
 * - Redefinição administrativa de senha - Validações de contexto (sessões
 * ativas, etc)
 * 
 * Portos utilizados: - Output Port: UserReader — buscar usuário - Output Port:
 * userStatusPort — atualizar status e senha - Output Port:
 * authenticationPort — validar sessões
 */
@Component
@Transactional
@RequiredArgsConstructor
public class ManageUserStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(ManageUserStatusUseCase.class);
    private static final String TEMP_PASSWORD = "SBEReset@#2026";

    private final UserQueryPort userQueryPort;
    private final UserStatusPort userStatusPort;
    private final AuthenticationPort authenticationPort;

    /**
     * Desativa um usuário ativo.
     * 
     * Regras: - Bloqueado se houver sessão ativa nos últimos 30 minutos
     * 
     * @param command contém ID do usuário e quem está fazendo a ação
     * @throws BusinessException se usuário já inativo ou tem sessão ativa
     */
    @InvalidateUserCache
    public void deactivateUser(UserManagementUseCase.StatusChangeCommand command) {
        log.info("Desativando usuário ID: {}", command.idUsuario());
        
        // Busca usuário
        User user = findUserOrThrow(command.idUsuario());

        // Valida se já está inativo
        if (user.isInactive()) {
            log.warn("Usuário já está inativo. ID: {}", command.idUsuario());
            throw new BusinessException("Usuário já está inativo.", "ALREADY_INACTIVE");
        }

        // Bloqueia se há sessão ativa nos últimos 30 minutos
        if (authenticationPort.hasActiveSession(command.idUsuario())) {
            log.warn("Tentativa de inativar usuário com sessão ativa. ID: {}", command.idUsuario());
            throw new BusinessException(
                "Não é possível inativar usuário com sessão ativa. Aguarde 30 minutos ou solicite logout.",
                "ACTIVE_SESSION");
        }

        // Inativa usuário
        userStatusPort.updateStatus(
            command.idUsuario(),
            UserStatus.INACTIVE.getCode(),
            command.idUsuarioLogado());
        
        log.info("Usuário desativado. ID: {}", command.idUsuario());
    }

    /**
     * Reativa um usuário inativo.
     * 
     * Regras: - Reseta contador de tentativas
     * 
     * @param command contém ID do usuário e quem está fazendo a ação
     * @throws BusinessException se usuário já está ativo
     */
    @InvalidateUserCache
    public void reactivateUser(UserManagementUseCase.StatusChangeCommand command) {
        log.info("Reativando usuário ID: {}", command.idUsuario());
        
        // Busca usuário
        User user = findUserOrThrow(command.idUsuario());

        // Valida se já está ativo
        if (user.isActived()) {
            log.warn("Usuário já está ativo. ID: {}", command.idUsuario());
            throw new BusinessException("Usuário já está ativo.", "ALREADY_ACTIVE");
        }

        // Reativa e zera tentativas
        authenticationPort.resetAttemptsAndStatus(
            command.idUsuario(),
            UserStatus.ACTIVE.getCode(),
            command.idUsuarioLogado());
        
        log.info("Usuário reativado. ID: {}", command.idUsuario());
    }

    /**
     * Desbloqueia um usuário bloqueado por excesso de tentativas.
     * 
     * Regras: - Reseta contador de tentativas - Operação administrativa
     * 
     * @param command contém ID do usuário e quem está fazendo a ação
     * @throws BusinessException se usuário não está bloqueado
     */
    @InvalidateUserCache
    public void unblockUser(UserManagementUseCase.StatusChangeCommand command) {
        log.info("Desbloqueando usuário ID: {}", command.idUsuario());
        
        // Busca usuário
        User user = findUserOrThrow(command.idUsuario());

        // Valida se está bloqueado
        if (!user.isBlocked()) {
            log.warn("Usuário não está bloqueado. ID: {}", command.idUsuario());
            throw new BusinessException("Usuário não está bloqueado.", "NOT_BLOCKED");
        }

        // Desbloqueia e zera contador de tentativas
        authenticationPort.resetAttemptsAndStatus(
            command.idUsuario(),
            UserStatus.ACTIVE.getCode(),
            command.idUsuarioLogado());
        
        log.info("Usuário desbloqueado. ID: {}", command.idUsuario());
    }

    /**
     * Redefinição administrativa de senha.
     * 
     * Regras: - Gera nova senha temporária - Define DT_EXPIRA_SENHA = now()
     * para forçar troca no próximo login - Não exige token — o administrador
     * que executa é a autorização
     * 
     * @param command contém ID do usuário e quem está executando
     * @return a senha temporária gerada
     * @throws ResourceNotFoundException se usuário não encontrado
     */
    @InvalidateUserCache
    public String adminResetPassword(UserManagementUseCase.AdminResetPasswordCommand command) {
        log.info("Reset administrativo de senha. Usuário ID: {}", command.idUsuario());
        
        // Busca usuário
        User user = findUserOrThrow(command.idUsuario());

        // Gera nova senha temporária
        String newHash = PasswordHashUtil.hashBcrypt(TEMP_PASSWORD);
        String oldHash = user.getCodSenha();

        // Atualiza senha (força expiração imediata)
        userStatusPort.updatePassword(
            command.idUsuario(),
            newHash,
            oldHash,
            LocalDateTime.now()); // DT_EXPIRA = agora, força troca imediata
        
        log.info("Senha administrativa resetada. Usuário ID: {}", command.idUsuario());
        
        return TEMP_PASSWORD;
    }

    /**
     * Atualiza configurações de jornada de acesso do usuário.
     * 
     * @param command contém ID, dias da semana e horários
     * @throws ResourceNotFoundException se usuário não encontrado
     */
    @InvalidateUserCache
    public void updateAccessSchedule(UserManagementUseCase.UpdateScheduleCommand command) {
        log.info("Atualizando jornada de acesso. Usuário ID: {}", command.idUsuario());
        
        // Valida existência do usuário
        findUserOrThrow(command.idUsuario());

        // Atualiza jornada
        userStatusPort.updateAccessSchedule(
                command.idUsuario(),
                command.numDiasSemanasPermitidos(),
                command.dtJornadaIni(),
                command.dtJornadaFim(),
                command.idUsuarioLogado());
        
        log.info("Jornada de acesso atualizada. Usuário ID: {}", command.idUsuario());
    }

    // ── Utilitário privado ────────────────────────────────────────────────────
    private User findUserOrThrow(Long idUsuario) {
        return userQueryPort.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", idUsuario));
    }
}
