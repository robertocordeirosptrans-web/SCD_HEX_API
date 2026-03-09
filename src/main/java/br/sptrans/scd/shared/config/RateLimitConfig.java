package br.sptrans.scd.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração do Rate Limiting
 * 
 * Registra o interceptor de rate limiting para todos os endpoints
 */
@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    public RateLimitConfig(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/actuator/health",  // Health check não deve ter rate limit
                    "/swagger-ui/**",     // Swagger UI
                    "/v3/api-docs/**",    // OpenAPI docs
                    "/h2-console/**"      // H2 console (apenas dev)
                );
    }
}
