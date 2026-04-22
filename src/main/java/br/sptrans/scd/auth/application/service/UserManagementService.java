package br.sptrans.scd.auth.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase;
import br.sptrans.scd.auth.application.usecases.user.CreateUserUseCase;
import br.sptrans.scd.auth.application.usecases.user.ManageUserStatusUseCase;
import br.sptrans.scd.auth.application.usecases.user.QueryUserUseCase;
import br.sptrans.scd.auth.application.usecases.user.UpdateUserUseCase;
import br.sptrans.scd.auth.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserManagementService implements UserManagementUseCase {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ManageUserStatusUseCase manageUserStatusUseCase;
    private final QueryUserUseCase queryUserUseCase;

    // ── Criando Usuario ────────────────────────────────────────────────────────────
    @Override
    public User createUser(CreateUserCommand cmd) {
        return createUserUseCase.createUser(cmd);
    }

    // ── updateUser ────────────────────────────────────────────────────────────
    @Override
    public User updateUser(UpdateUserCommand cmd) {
        return updateUserUseCase.updateUser(cmd);
    }

    // ── deactivateUser ────────────────────────────────────────────────────────
    @Override
    public void deactivateUser(StatusChangeCommand cmd) {
        manageUserStatusUseCase.deactivateUser(cmd);
    }

    // ── reactivateUser ────────────────────────────────────────────────────────
    @Override
    public void reactivateUser(StatusChangeCommand cmd) {
        manageUserStatusUseCase.reactivateUser(cmd);
    }

    // ── unblockUser ─────────────────────────────────────────────────────────────────────────
    @Override
    public void unblockUser(StatusChangeCommand cmd) {
        manageUserStatusUseCase.unblockUser(cmd);
    }

    // ── adminResetPassword ────────────────────────────────────────────────────
    @Override
    public String adminResetPassword(AdminResetPasswordCommand cmd) {
        return manageUserStatusUseCase.adminResetPassword(cmd);
    }

    // ── updateAccessSchedule ──────────────────────────────────────────────────
    @Override
    public void updateAccessSchedule(UpdateScheduleCommand cmd) {
        manageUserStatusUseCase.updateAccessSchedule(cmd);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listUsersPaginated(UserManagementUseCase.UserFilterRequest filtro, int page, int size, String sortBy, String sortDir) {
        return queryUserUseCase.listUsersPaginated(filtro, page, size, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> listUsersPaginated(Specification<UserEntityJpa> spec, Pageable pageable) {
        return queryUserUseCase.listUsersPaginated(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsers(UserManagementUseCase.UserFilterRequest filtro) {
        return queryUserUseCase.countUsers(filtro);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long idUsuario) {
        return queryUserUseCase.findById(idUsuario);
    }

}
