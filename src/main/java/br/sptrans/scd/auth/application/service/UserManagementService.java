package br.sptrans.scd.auth.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.out.AuthenticationRepository;
import br.sptrans.scd.auth.application.port.out.UserReader;
import br.sptrans.scd.auth.application.port.out.UserStatusRepository;
import br.sptrans.scd.auth.application.port.out.UserWriter;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.shared.cache.InvalidateUserCache;
import br.sptrans.scd.shared.exception.BusinessException;
import br.sptrans.scd.shared.exception.DuplicateResourceException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.security.PasswordHashUtil;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserManagementService implements UserManagementUseCase {

    private static final String TEMP_PASSWORD = "SBEReset@#2026";

    private final UserReader userReader;
    private final UserWriter userWriter;
    private final AuthenticationRepository authenticationRepository;
    private final UserStatusRepository userStatusRepository;

    // ── Criando Usuario ────────────────────────────────────────────────────────────
    @Override
    @InvalidateUserCache
    public User createUser(CreateUserCommand cmd) {
        // COD_LOGIN único
        if (userReader.existsByLogin(cmd.codLogin())) {
            throw new DuplicateResourceException("Login", "codLogin", cmd.codLogin());
        }

        String passwordHash = PasswordHashUtil.hashBcrypt(TEMP_PASSWORD);

        User user = new User();
        user.setCodLogin(cmd.codLogin().trim().toLowerCase());
        user.setNomUsuario(cmd.nomUsuario().trim());
        user.setNomEmail(cmd.nomEmail().trim());
        user.setCodCpf(cmd.codCpf());
        user.setCodRg(cmd.codRg());
        user.setCodSenha(passwordHash);
        user.setSenhaAntiga(null);
        user.setCodStatus(UserStatus.ACTIVE);
        user.setNumTentativasFalha(0);
        user.setNumDiasSemanasPermitidos(cmd.numDiasSemanasPermitidos());
        user.setDtJornadaIni(cmd.dtJornadaIni());
        user.setDtJornadaFim(cmd.dtJornadaFim());
        // DT_EXPIRA_SENHA = 3 meses após criação
        user.setDtExpiraSenha(LocalDateTime.now().plusMonths(3));
        user.setDtCriacao(LocalDateTime.now());
        user.setDtModi(LocalDateTime.now());
        // Força troca de senha no primeiro acesso com a senha temporária
        user.setDtExpiraSenha(LocalDateTime.now());

        userWriter.save(user);
        return user;
    }

    // ── updateUser ────────────────────────────────────────────────────────────
    @Override
    @InvalidateUserCache
    public User updateUser(UpdateUserCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        // COD_LOGIN é imutável — não há campo para alterar aqui
        user.setNomUsuario(cmd.nomUsuario().trim());
        user.setNomEmail(cmd.nomEmail().trim());
        user.setCodCpf(cmd.codCpf());
        user.setCodRg(cmd.codRg());
        user.setDtModi(LocalDateTime.now());

        userWriter.update(user);
        return user;
    }

    // ── deactivateUser ────────────────────────────────────────────────────────
    @Override
    @InvalidateUserCache
    public void deactivateUser(StatusChangeCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        if (user.isInactive()) {
            throw new BusinessException("Usuário já está inativo.", "ALREADY_INACTIVE");
        }

        // Bloqueia inativação se há sessão ativa nos últimos 30 minutos
        if (authenticationRepository.hasActiveSession(cmd.idUsuario())) {
            throw new BusinessException("Não é possível inativar usuário com sessão ativa. Aguarde 30 minutos ou solicite logout.", "ACTIVE_SESSION");
        }

        userStatusRepository.updateStatus(cmd.idUsuario(), br.sptrans.scd.auth.domain.enums.UserStatus.INACTIVE.getCode(), cmd.idUsuarioLogado());
    }

    // ── reactivateUser ────────────────────────────────────────────────────────
    @Override
    @InvalidateUserCache
    public void reactivateUser(StatusChangeCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        if (user.isActived()) {
            throw new BusinessException("Usuário já está ativo.", "ALREADY_ACTIVE");
        }

        // Reativa e zera tentativas
        authenticationRepository.resetAttemptsAndStatus(cmd.idUsuario(), br.sptrans.scd.auth.domain.enums.UserStatus.ACTIVE.getCode(), cmd.idUsuarioLogado());
    }

    // ── unblockUser ─────────────────────────────────────────────────────────────────────────
    @Override
    @InvalidateUserCache
    public void unblockUser(StatusChangeCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        if (!user.isBlocked()) {
            throw new BusinessException("Usuário não está bloqueado.", "NOT_BLOCKED");
        }

        // Desbloqueia e zera contador de tentativas
        authenticationRepository.resetAttemptsAndStatus(cmd.idUsuario(), br.sptrans.scd.auth.domain.enums.UserStatus.ACTIVE.getCode(), cmd.idUsuarioLogado());
    }

    // ── adminResetPassword ────────────────────────────────────────────────────
    @Override
    @InvalidateUserCache
    public String adminResetPassword(AdminResetPasswordCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        String newHash = PasswordHashUtil.hashBcrypt(TEMP_PASSWORD);
        String oldHash = user.getCodSenha(); // preserva senha atual como "anterior"

        userStatusRepository.updatePassword(cmd.idUsuario(), newHash, oldHash, LocalDateTime.now());

        return TEMP_PASSWORD;
    }

    // ── updateAccessSchedule ──────────────────────────────────────────────────
    @Override
    @InvalidateUserCache
    public void updateAccessSchedule(UpdateScheduleCommand cmd) {
        findUserOrThrow(cmd.idUsuario()); // garante existência

        userStatusRepository.updateAccessSchedule(
                cmd.idUsuario(),
                cmd.numDiasSemanasPermitidos(),
                cmd.dtJornadaIni(),
                cmd.dtJornadaFim(),
                cmd.idUsuarioLogado());
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "usuarios", keyGenerator = "listUsersPaginatedKeyGenerator")
    public List<User> listUsersPaginated(UserManagementUseCase.UserFilterRequest filtro, int page, int size, String sortBy, String sortDir) {
        int offset = page * size;
        // Adapte o repositório para aceitar os novos filtros se necessário
        return userReader.findAllPaginated(
            filtro.codStatus(),
            filtro.nomUsuario(),
            filtro.nomEmail(),
            filtro.codPerfil(),
            offset,
            size,
            sortBy,
            sortDir
        );
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "usuarios", keyGenerator = "countUsersKeyGenerator")
    public long countUsers(UserManagementUseCase.UserFilterRequest filtro) {
        // Adapte o repositório para aceitar os novos filtros se necessário
        return userReader.countAll(
            filtro.codStatus(),
            filtro.nomUsuario(),
            filtro.nomEmail(),
            filtro.codPerfil()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long idUsuario) {
        return findUserOrThrow(idUsuario);
    }

    // ── Utilitários privados ──────────────────────────────────────────────────
    private User findUserOrThrow(Long idUsuario) {
        return userReader.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", idUsuario));
    }

}
