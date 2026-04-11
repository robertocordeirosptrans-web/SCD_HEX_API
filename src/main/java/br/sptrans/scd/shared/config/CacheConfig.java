package br.sptrans.scd.shared.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${api.security.token.expiration-hours:2}")
    private int sessionTtlHours;

    @Bean
    public CacheManager cacheManager() {
        List<Cache> caches = new ArrayList<>();

        // Caches gerais — sem expiração automática
        for (String name : List.of("usuarios", "canais", "produtos", "pedidos",
                "order-list", "productPeriodReport", "permissoes")) {
            caches.add(new CaffeineCache(name,
                    Caffeine.newBuilder()
                            .maximumSize(500)
                            .build()));
        }

        // Cache de sessões — expira junto com o JWT para manter coerência
        caches.add(new CaffeineCache("sessoes",
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(sessionTtlHours, TimeUnit.HOURS)
                        .build()));

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(caches);
        return manager;
    }

    /**
     * KeyGenerator para listUsersPaginated().
     */
    @Bean(name = "listUsersPaginatedKeyGenerator")
    public KeyGenerator listUsersPaginatedKeyGenerator() {
        return new UserCacheKeyGenerators.ListUsersPaginatedKeyGenerator();
    }

    /**
     * KeyGenerator para countUsers().
     */
    @Bean(name = "countUsersKeyGenerator")
    public KeyGenerator countUsersKeyGenerator() {
        return new UserCacheKeyGenerators.CountUsersKeyGenerator();
    }
}

