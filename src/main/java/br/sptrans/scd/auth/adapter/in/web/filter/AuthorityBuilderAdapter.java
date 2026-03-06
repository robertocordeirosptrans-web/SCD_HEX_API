package br.sptrans.scd.auth.adapter.in.web.filter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.domain.User;


@Component
public class AuthorityBuilderAdapter {

    public List<GrantedAuthority> buildAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Sempre tem ROLE_USER
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // Perfis do usuário → PERFIL_ADMIN, PERFIL_OPERADOR, etc.
        if (user.getProfiles() != null) {
            user.getProfiles().stream()
                .filter(p -> "A".equals(p.getCodStatus()))
                .forEach(p -> {
                    // ROLE_ADMIN para perfil ADMIN
                    if ("ADMIN".equals(p.getCodPerfil())) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                    authorities.add(new SimpleGrantedAuthority("PERFIL_" + p.getCodPerfil()));
                });
        }

        // Funcionalidades diretas → FUNC_SISTEMA_MODULO_ROTINA
        if (user.getFunctionalities() != null) {
            user.getFunctionalities().stream()
                .filter(f -> "A".equals(f.getCodStatus()))
                .forEach(f -> authorities.add(
                    new SimpleGrantedAuthority(
                        "FUNC_" + f.getCodSistema() + "_" + f.getCodModulo() + "_" + f.getCodRotina()
                    )
                ));
        }

        return authorities;
    }
}