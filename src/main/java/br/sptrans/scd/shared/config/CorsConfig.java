package br.sptrans.scd.shared.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuração CORS por ambiente
 * 
 * Permite configurar origens permitidas, métodos HTTP, headers e credenciais
 * de forma diferente para cada ambiente (dev, hml, prd)
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:*}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cors.exposed-headers:Authorization,Content-Type}")
    private String[] exposedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    /**
     * Configura o CORS de acordo com as propriedades do ambiente
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configuração de origens permitidas
        if (allowedOrigins.length == 1 && "*".equals(allowedOrigins[0])) {
            // Se for *, usa allowedOriginPatterns para compatibilidade com allowCredentials
            configuration.setAllowedOriginPatterns(List.of("*"));
        } else {
            // Caso contrário, usa origens específicas
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        }
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        
        // Headers expostos na resposta
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders));
        
        // Permitir credenciais (cookies, authorization headers)
        configuration.setAllowCredentials(allowCredentials);
        
        // Tempo de cache da configuração CORS (em segundos)
        configuration.setMaxAge(maxAge);
        
        // Registra a configuração para todos os endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
