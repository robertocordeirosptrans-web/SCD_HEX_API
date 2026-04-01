package br.sptrans.scd.auth.domain.vo;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.Profile;
import lombok.Value;

/**
 * Value Object: contexto de autorização do usuário.
 * Carregado sob demanda após autenticação — não persistido junto com User.
 */
@Value
public class AuthorizationContext {

    Set<Profile> perfis;
    Set<Functionality> funcionalidades;
    Set<Group> grupos;

    public AuthorizationContext(Set<Profile> perfis, Set<Functionality> funcionalidades, Set<Group> grupos) {
        this.perfis = perfis != null ? Collections.unmodifiableSet(perfis) : Set.of();
        this.funcionalidades = funcionalidades != null ? Collections.unmodifiableSet(funcionalidades) : Set.of();
        this.grupos = grupos != null ? Collections.unmodifiableSet(grupos) : Set.of();
    }

    public Set<String> getCodPerfis() {
        return perfis.stream()
                .map(Profile::getCodPerfil)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getChavesFuncionalidades() {
        return funcionalidades.stream()
                .map(Functionality::getCodFuncionalidade)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getCodGrupos() {
        return grupos.stream()
                .map(Group::getCodGrupo)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static AuthorizationContext vazio() {
        return new AuthorizationContext(Set.of(), Set.of(), Set.of());
    }
}
