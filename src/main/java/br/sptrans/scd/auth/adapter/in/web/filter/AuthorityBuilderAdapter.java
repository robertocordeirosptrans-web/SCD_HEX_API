package br.sptrans.scd.auth.adapter.in.web.filter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.User;


@Component
public class AuthorityBuilderAdapter {

    private final UserPersistencePort userRepository;

    public AuthorityBuilderAdapter(UserPersistencePort userRepository) {
        this.userRepository = userRepository;
    }

    public List<GrantedAuthority> buildAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // Perfis efetivos (diretos + via grupos)
        for (Profile perfil : userRepository.carregarPerfisEfetivos(user.getIdUsuario())) {
            if ("ADMIN".equals(perfil.getCodPerfil())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            authorities.add(new SimpleGrantedAuthority("ROLE_" + perfil.getCodPerfil()));
        }

        // Funcionalidades efetivas (diretas + via perfis + via grupos)
        for (Functionality func : userRepository.carregarFuncionalidadesEfetivas(user.getIdUsuario())) {
            authorities.add(new SimpleGrantedAuthority(func.canonicalKey()));
        }

        return authorities;
    }
}