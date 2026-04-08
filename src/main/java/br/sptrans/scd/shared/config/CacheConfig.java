package br.sptrans.scd.shared.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "usuarios",
                "canais",
                "produtos",
                "pedidos",
                "order-list",
                "productPeriodReport",
                "permissoes"
        );
    }

    /**
     * KeyGenerator para listUsersPaginated().
     * Substitui SpEL expression gigante por código legível.
     */
    @Bean(name = "listUsersPaginatedKeyGenerator")
    public KeyGenerator listUsersPaginatedKeyGenerator() {
        return new UserCacheKeyGenerators.ListUsersPaginatedKeyGenerator();
    }

    /**
     * KeyGenerator para countUsers().
     * Substitui SpEL expression gigante por código legível.
     */
    @Bean(name = "countUsersKeyGenerator")
    public KeyGenerator countUsersKeyGenerator() {
        return new UserCacheKeyGenerators.CountUsersKeyGenerator();
    }

}
