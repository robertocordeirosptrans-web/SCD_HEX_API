package br.sptrans.scd.shared.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
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
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "usuarios",
                "canais",
                "produtos",
                "pedidos",
                "order-list",
                "productPeriodReport",
                "permissoes"
        );
        // TTL garante que permissões concedidas diretamente no banco (fora da API)
        // sejam visíveis em no máximo 5 minutos, sem precisar reiniciar a aplicação.
        manager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES));
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

