package br.sptrans.scd.shared.config;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.cache.UserCountCacheKey;
import br.sptrans.scd.auth.application.cache.UserListCacheKey;
import br.sptrans.scd.auth.application.port.in.UserManagementUseCase.UserFilterRequest;

/**
 * KeyGenerators customizados para cache de usuários.
 * Substituem SpEL expressions gigantes por código legível e determinístico.
 */
@Component
public class UserCacheKeyGenerators {

    /**
     * Gera chave para listUsersPaginated().
     * Usa record imutável com toString() determinístico.
     *
     * Método: listUsersPaginated(UserFilterRequest filtro, int page, int size, String sortBy, String sortDir)
     */
    public static class ListUsersPaginatedKeyGenerator implements KeyGenerator {

        @Override
        public Object generate(Object target, Method method, Object... params) {
            UserFilterRequest filtro = (UserFilterRequest) params[0];
            int page = (int) params[1];
            int size = (int) params[2];
            String sortBy = (String) params[3];
            String sortDir = (String) params[4];

            return new UserListCacheKey(
                    filtro.codStatus() != null ? filtro.codStatus() : "ALL",
                    filtro.nomUsuario() != null ? filtro.nomUsuario() : "",
                    filtro.nomEmail() != null ? filtro.nomEmail() : "",
                    filtro.codPerfil() != null ? filtro.codPerfil() : "",
                    page,
                    size,
                    sortBy,
                    sortDir);
        }
    }

    /**
     * Gera chave para countUsers().
     * Usa record imutável com toString() determinístico.
     *
     * Método: countUsers(UserFilterRequest filtro)
     */
    public static class CountUsersKeyGenerator implements KeyGenerator {

        @Override
        public Object generate(Object target, Method method, Object... params) {
            UserFilterRequest filtro = (UserFilterRequest) params[0];

            return new UserCountCacheKey(
                    filtro.codStatus() != null ? filtro.codStatus() : "ALL",
                    filtro.nomUsuario() != null ? filtro.nomUsuario() : "",
                    filtro.nomEmail() != null ? filtro.nomEmail() : "",
                    filtro.codPerfil() != null ? filtro.codPerfil() : "");
        }
    }
}
