package br.sptrans.scd.auth.domain.vo;

import java.security.Permission;
import java.util.Set;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.Profile;
import lombok.Value;

@Value
public class AuthorizationContext {

    private final Set<Profile> roles;
    private final Set<Permission> permissions;
    private final Set<Group> groups;

    // Método factory para carregar sob demanda
    public static AuthorizationContext loadFor(UserId userId, UserRepository repo) {
        var roles = repo.loadRoles(userId);
        var permissions = repo.loadPermissions(userId);
        var groups = repo.loadGroups(userId);
        return new AuthorizationContext(roles, permissions, groups);
    }
}
