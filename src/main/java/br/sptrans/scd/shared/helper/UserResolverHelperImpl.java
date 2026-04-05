package br.sptrans.scd.shared.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.exception.AuthenticationFailedException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserResolverHelperImpl implements UserResolverHelper {

    private final UserPersistencePort userPort;

    @Override
    public User resolve(Long userId) {
        if (userId == null) return null;
        return userPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));
    }

    @Override
    public String getCurrentLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationFailedException("Usuário não autenticado");
        }
        return authentication.getName();
    }

    @Override
    public User getCurrentUser() {
        String login = getCurrentLogin();
        return userPort.findByCodLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "login", login));
    }

    @Override
    public Long getCurrentUserId() {
        return getCurrentUser().getIdUsuario();
    }
}

