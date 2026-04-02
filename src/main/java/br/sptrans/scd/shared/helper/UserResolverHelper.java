package br.sptrans.scd.shared.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.auth.domain.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserResolverHelper {

    private final UserPersistencePort userPort;

    public Long resolveId(Authentication auth) {
        return userPort.findByCodLogin(auth.getName())
                .map(User::getIdUsuario)
                .orElseThrow(() -> new UsernameNotFoundException(
                "Usuário autenticado não encontrado: " + auth.getName()));
    }
}
