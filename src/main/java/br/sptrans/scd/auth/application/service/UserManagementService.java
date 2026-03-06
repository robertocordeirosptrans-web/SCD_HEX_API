package br.sptrans.scd.auth.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.UserManagementException;
import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.shared.exception.EncryptorException;
import br.sptrans.scd.shared.security.Criptografia;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserManagementService implements UserManagementUseCase {

    private final UserRepository userRepository;

    // ── Criando Usuario ────────────────────────────────────────────────────────────
    @Override
    public User createUser(CreateUserCommand cmd) {
        // COD_LOGIN único
        if (userRepository.existsByLogin(cmd.codLogin())) {
            throw new UserManagementException(
                    ErrorType.LOGIN_ALREADY_EXISTS,
                    "Login '" + cmd.codLogin() + "' já está em uso.");
        }

        String tempPassword = "SBEReset@#2026";

        String passwordHash;
        try {
            passwordHash = Criptografia.encripta(tempPassword);
        } catch (br.sptrans.scd.shared.exception.EncryptorException e) {
            throw new RuntimeException("Erro ao gerar hash JWT da senha", e);
        }

        User user = new User();
        user.setCodLogin(cmd.codLogin().trim().toLowerCase());
        user.setNomUsuario(cmd.nomUsuario().trim());
        user.setNomEmail(cmd.nomEmail().trim());
        user.setCodCpf(cmd.codCpf());
        user.setCodRg(cmd.codRg());
        user.setCodSenha(passwordHash);
        user.setOldSenha(null);
        user.setStatus(UserStatus.ACTIVE);
        user.setNumTentativasFalha(0);
        user.setNumDiasSemanasPermitidos(cmd.numDiasSemanasPermitidos());
        user.setDt_jornada_ini(cmd.dtJornadaIni());
        user.setDt_jornada_fim(cmd.dtJornadaFim());
        // DT_EXPIRA_SENHA = 3 meses após criação
        user.setDtExpiraSenha(LocalDateTime.now().plusMonths(3));
        user.setDtCriacao(LocalDateTime.now());
        user.setDtModi(LocalDateTime.now());
        // Força troca de senha se senha temporária for SBEReset@#2026
        if (tempPassword.equals("SBEReset@#2026")) {
            user.setDtExpiraSenha(LocalDateTime.now());
        }

        userRepository.insert(user);
        return user;
    }

    // ── updateUser ────────────────────────────────────────────────────────────
    @Override
    public User updateUser(UpdateUserCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        // COD_LOGIN é imutável — não há campo para alterar aqui
        user.setNomUsuario(cmd.nomUsuario().trim());
        user.setNomEmail(cmd.nomEmail().trim());
        user.setCodCpf(cmd.codCpf());
        user.setCodRg(cmd.codRg());
        user.setDtModi(LocalDateTime.now());

        userRepository.update(user);
        return user;
    }

    // ── deactivateUser ────────────────────────────────────────────────────────
    @Override
    public void deactivateUser(StatusChangeCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        if (user.isInactive()) {
            throw new UserManagementException(
                    ErrorType.ALREADY_INACTIVE,
                    "Usuário já está inativo.");
        }

        // Bloqueia inativação se há sessão ativa nos últimos 30 minutos
        if (userRepository.hasActiveSession(cmd.idUsuario())) {
            throw new UserManagementException(
                    ErrorType.ACTIVE_SESSION,
                    "Não é possível inativar usuário com sessão ativa. Aguarde 30 minutos ou solicite logout.");
        }

        userRepository.updateStatus(cmd.idUsuario(), br.sptrans.scd.auth.domain.enums.UserStatus.INACTIVE.getCode(), cmd.idUsuarioLogado());
    }

    // ── reactivateUser ────────────────────────────────────────────────────────
    @Override
    public void reactivateUser(StatusChangeCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        if (user.isActived()) {
            throw new UserManagementException(
                    ErrorType.ALREADY_ACTIVE,
                    "Usuário já está ativo.");
        }

        // Reativa e zera tentativas
        userRepository.resetAttemptsAndStatus(cmd.idUsuario(), br.sptrans.scd.auth.domain.enums.UserStatus.ACTIVE.getCode(), cmd.idUsuarioLogado());
    }

    // ── unblockUser ───────────────────────────────────────────────────────────
    @Override
    public void unblockUser(StatusChangeCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        if (!user.isBlocked()) {
            throw new UserManagementException(
                    ErrorType.NOT_BLOCKED,
                    "Usuário não está bloqueado.");
        }

        // Desbloqueia e zera contador de tentativas
        userRepository.resetAttemptsAndStatus(cmd.idUsuario(), br.sptrans.scd.auth.domain.enums.UserStatus.ACTIVE.getCode(), cmd.idUsuarioLogado());
    }

    // ── adminResetPassword ────────────────────────────────────────────────────
    @Override
    public String adminResetPassword(AdminResetPasswordCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        // Nova senha temporária
        String tempPassword = "SBEReset@#2026";

        String newHash;
        try {
            newHash = Criptografia.encripta(tempPassword);
        } catch (EncryptorException e) {
            throw new RuntimeException("Erro ao gerar hash JWT da senha", e);
        }
        String oldHash = user.getCodSenha(); // preserva senha atual como "anterior"

        userRepository.updatePassword(cmd.idUsuario(), newHash, oldHash, LocalDateTime.now());

        return tempPassword;
    }

    // ── updateAccessSchedule ──────────────────────────────────────────────────
    @Override
    public void updateAccessSchedule(UpdateScheduleCommand cmd) {
        findUserOrThrow(cmd.idUsuario()); // garante existência

        userRepository.updateAccessSchedule(
                cmd.idUsuario(),
                cmd.numDiasSemanasPermitidos(),
                cmd.dtJornadaIni(),
                cmd.dtJornadaFim(),
                cmd.idUsuarioLogado());
    }

    // ── listUsers ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<User> listUsers(String codStatus) {
        return userRepository.findAll(codStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long idUsuario) {
        return findUserOrThrow(idUsuario);
    }

    // ── Utilitários privados ──────────────────────────────────────────────────
    private User findUserOrThrow(Long idUsuario) {
        return userRepository.findById(idUsuario)
                .orElseThrow(() -> new UserManagementException(
                ErrorType.USER_NOT_FOUND,
                "Usuário não encontrado: id=" + idUsuario));
    }

}
