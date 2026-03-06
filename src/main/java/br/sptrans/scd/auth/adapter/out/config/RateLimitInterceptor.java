package br.sptrans.scd.auth.adapter.out.config;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Rate Limiting Interceptor usando Bucket4j
 * 
 * Implementa proteção contra:
 * - Força bruta em endpoints de autenticação
 * - DoS (Denial of Service) attacks
 * - API abuse
 * 
 * CRITICIDADE: ALTA
 * OWASP: A07:2021 - Identification and Authentication Failures
 * 
 * Configuração por tipo de endpoint:
 * - Login: 5 tentativas por minuto por IP
 * - API: 100 requisições por minuto por usuário
 * - Public: 50 requisições por minuto por IP
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Value("${rate-limiting.enabled:false}")
    private boolean rateLimitingEnabled;

    @Value("${rate-limiting.login.capacity:5}")
    private int loginCapacity;

    @Value("${rate-limiting.api.capacity:100}")
    private int apiCapacity;

    @Value("${rate-limiting.public.capacity:50}")
    private int publicCapacity;

    @Override
    public boolean preHandle(@SuppressWarnings("null") HttpServletRequest request, @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") Object handler) 
            throws Exception {
        
        if (!rateLimitingEnabled) {
            return true;
        }

        String key = resolveKey(request);
        Bucket bucket = resolveBucket(key, request.getRequestURI());

        if (bucket.tryConsume(1)) {
            // Request permitida
            long availableTokens = bucket.getAvailableTokens();
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(availableTokens));
            return true;
        } else {
            // Rate limit excedido
            log.warn("Rate limit exceeded for key: {} on URI: {}", key, request.getRequestURI());
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"message\": \"Too many requests. Please try again later.\", " +
                "\"errorCode\": \"RATE_LIMIT_EXCEEDED\"}"
            );
            
            // Header Retry-After (em segundos)
            response.addHeader("Retry-After", "60");
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", "60");
            
            return false;
        }
    }

    /**
     * Resolve a chave para o bucket baseado em IP ou usuário
     */
    private String resolveKey(HttpServletRequest request) {
        // Tenta obter o usuário autenticado
        String user = request.getRemoteUser();
        if (user != null) {
            return "user:" + user;
        }
        
        // Caso contrário, usa o IP (considerando proxy)
        String ip = getClientIP(request);
        return "ip:" + ip;
    }

    /**
     * Obtém o IP real do cliente, considerando proxies
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Cria ou recupera um bucket para a chave
     */
    private Bucket resolveBucket(String key, String uri) {
        return cache.computeIfAbsent(key, k -> createNewBucket(uri));
    }

    /**
     * Cria um novo bucket com limites baseados no tipo de endpoint
     */
    private Bucket createNewBucket(String uri) {
        Bandwidth limit;
        
        if (uri.contains("/auth/login") || uri.contains("/auth/forgot-password")) {
            // Limite mais restritivo para endpoints de autenticação
            limit = Bandwidth.classic(loginCapacity, Refill.intervally(loginCapacity, Duration.ofMinutes(1)));
        } else if (uri.startsWith("/api/")) {
            // Limite padrão para APIs
            limit = Bandwidth.classic(apiCapacity, Refill.intervally(apiCapacity, Duration.ofMinutes(1)));
        } else {
            // Limite para endpoints públicos
            limit = Bandwidth.classic(publicCapacity, Refill.intervally(publicCapacity, Duration.ofMinutes(1)));
        }
        
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
