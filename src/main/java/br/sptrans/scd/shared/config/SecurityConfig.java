package br.sptrans.scd.shared.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.sptrans.scd.audit.application.port.in.AuditUseCase;
import br.sptrans.scd.audit.domain.AuditEvent;
import br.sptrans.scd.audit.domain.AuditEventType;
import br.sptrans.scd.auth.adapter.in.web.filter.JwtAuthFilter;
import br.sptrans.scd.shared.audit.AuditContext;
import br.sptrans.scd.shared.audit.AuditContextFilter;
import br.sptrans.scd.shared.exception.dto.ErrorResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuditContextFilter auditContextFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final List<HeaderWriter> securityHeaderWriters;
    private final ObjectMapper objectMapper;
    private final AuditUseCase auditUseCase;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
            AuditContextFilter auditContextFilter,
            CorsConfigurationSource corsConfigurationSource,
            List<HeaderWriter> securityHeaderWriters,
            ObjectMapper objectMapper,
            AuditUseCase auditUseCase) {
        this.jwtAuthFilter          = jwtAuthFilter;
        this.auditContextFilter     = auditContextFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.securityHeaderWriters  = securityHeaderWriters;
        this.objectMapper           = objectMapper;
        this.auditUseCase           = auditUseCase;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // ── Security Headers (OWASP) ─────────────────────────────────
                .headers(h -> h
                .frameOptions(f -> f.deny()) // Anti-Clickjacking
                .xssProtection(x -> x.disable()) // CSP é suficiente
                .referrerPolicy(r -> r.policy(
                ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .httpStrictTransportSecurity(hsts -> hsts
                .includeSubDomains(true)
                .maxAgeInSeconds(31_536_000)
                .preload(true))
                .addHeaderWriter((req, res) -> {
                    res.setHeader("Server", "");
                    res.setHeader("X-Powered-By", "");
                })
                )
                // ── Autorização de Rotas ──────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/v1/auth/login",
                        "/api/v1/auth/forgot-password",
                        "/api/v1/auth/reset-password",
                        "/actuator/health",
                        "/swagger-ui/**", "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
                )
                // ── Custom Error Responses ────────────────────────────────────
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    AuditContext.Data ctx = AuditContext.get();
                    auditUseCase.audit(AuditEvent.preAuth(
                            AuditEventType.REQUEST_UNAUTHORIZED,
                            ctx.ipAddress, ctx.userAgent, null));
                    res.setStatus(401);
                    res.setContentType("application/json");
                    var error = new ErrorResponse(401, "Unauthorized", "Token inválido ou ausente",
                            "UNAUTHORIZED", req.getRequestURI());
                    objectMapper.writeValue(res.getOutputStream(), error);
                })
                .accessDeniedHandler((req, res, e) -> {
                    AuditContext.Data ctx = AuditContext.get();
                    auditUseCase.audit(AuditEvent.of(
                            AuditEventType.ACCESS_DENIED,
                            ctx.userId, ctx.sessionId,
                            ctx.ipAddress, ctx.userAgent,
                            "{\"path\":\"" + req.getRequestURI() + "\"}"));
                    res.setStatus(403);
                    res.setContentType("application/json");
                    var error = new ErrorResponse(403, "Forbidden", "Acesso negado",
                            "ACCESS_DENIED", req.getRequestURI());
                    objectMapper.writeValue(res.getOutputStream(), error);
                })
                )
                .addFilterBefore(auditContextFilter, JwtAuthFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Adiciona headers customizados (CSP, Permissions-Policy, etc.)
        for (HeaderWriter hw : securityHeaderWriters) {
            http.headers(h -> h.addHeaderWriter(hw));
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
