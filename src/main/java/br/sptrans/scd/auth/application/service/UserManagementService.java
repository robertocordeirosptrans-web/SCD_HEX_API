package br.sptrans.scd.auth.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.shared.exception.BusinessException;
import br.sptrans.scd.shared.exception.DuplicateResourceException;
import br.sptrans.scd.shared.exception.EncryptorException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.security.Criptografia;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserManagementService implements UserManagementUseCase {

    private final UserRepository userRepository;

    // ── Criando Usuario ────────────────────────────────────────────────────────────
    @Override
    @CacheEvict(value = "usuarios", allEntries = true)
    public User createUser(CreateUserCommand cmd) {
        // COD_LOGIN único
        if (userRepository.existsByLogin(cmd.codLogin())) {
            throw new DuplicateResourceException("Login", "codLogin", cmd.codLogin());
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
        // Força troca de senha se senha temporária for SBEReset@#2026
        if (tempPassword.equals("SBEReset@#2026")) {
            user.setDtExpiraSenha(LocalDateTime.now());
        }

        userRepository.save(user);
        return user;
    }

    // ── updateUser ────────────────────────────────────────────────────────────
    @Override
    @CacheEvict(value = "usuarios", allEntries = true)
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
    @CacheEvict(value = "usuarios", allEntries = true)
    public void deactivateUser(StatusChangeCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        if (user.isInactive()) {
            throw new BusinessException("Usuário já está inativo.", "ALREADY_INACTIVE");
        }

        // Bloqueia inativação se há sessão ativa nos últimos 30 minutos
        if (userRepository.hasActiveSession(cmd.idUsuario())) {
            throw new BusinessException("Não é possível inativar usuário com sessão ativa. Aguarde 30 minutos ou solicite logout.", "ACTIVE_SESSION");
        }

        userRepository.updateStatus(cmd.idUsuario(), br.sptrans.scd.auth.domain.enums.UserStatus.INACTIVE.getCode(), cmd.idUsuarioLogado());
    }

    // ── reactivateUser ────────────────────────────────────────────────────────
    @Override
    @CacheEvict(value = "usuarios", allEntries = true)
    public void reactivateUser(StatusChangeCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        if (user.isActived()) {
            throw new BusinessException("Usuário já está ativo.", "ALREADY_ACTIVE");
        }

        // Reativa e zera tentativas
        userRepository.resetAttemptsAndStatus(cmd.idUsuario(), br.sptrans.scd.auth.domain.enums.UserStatus.ACTIVE.getCode(), cmd.idUsuarioLogado());
    }

    // ── unblockUser ───────────────────────────────────────────────────────────
    @Override
    @CacheEvict(value = "usuarios", allEntries = true)
    public void unblockUser(StatusChangeCommand cmd) {
        User user = findUserOrThrow(cmd.idUsuario());

        if (!user.isBlocked()) {
            throw new BusinessException("Usuário não está bloqueado.", "NOT_BLOCKED");
        }

        // Desbloqueia e zera contador de tentativas
        userRepository.resetAttemptsAndStatus(cmd.idUsuario(), br.sptrans.scd.auth.domain.enums.UserStatus.ACTIVE.getCode(), cmd.idUsuarioLogado());
    }

    // ── adminResetPassword ────────────────────────────────────────────────────
    @Override
    @CacheEvict(value = "usuarios", allEntries = true)
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


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "usuarios", key = "(#filtro.codStatus() ?: 'ALL') + '_' + (#filtro.nomUsuario() ?: '') + '_' + (#filtro.nomEmail() ?: '') + '_' + (#filtro.codPerfil() ?: '') + '_' + #page + '_' + #size + '_' + #sortBy + '_' + #sortDir")
    public List<User> listUsersPaginated(br.sptrans.scd.auth.adapter.port.in.rest.dto.UserFilterRequestDTO filtro, int page, int size, String sortBy, String sortDir) {
        int offset = page * size;
        // Adapte o repositório para aceitar os novos filtros se necessário
        return userRepository.findAllPaginated(
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
    @Cacheable(value = "usuarios", key = "'count_' + (#filtro.codStatus() ?: 'ALL') + '_' + (#filtro.nomUsuario() ?: '') + '_' + (#filtro.nomEmail() ?: '') + '_' + (#filtro.codPerfil() ?: '')")
    public long countUsers(br.sptrans.scd.auth.adapter.port.in.rest.dto.UserFilterRequestDTO filtro) {
        // Adapte o repositório para aceitar os novos filtros se necessário
        return userRepository.countAll(
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
        return userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", idUsuario));
    }

}
