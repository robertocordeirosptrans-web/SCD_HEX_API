package br.sptrans.scd.shared.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

import br.sptrans.scd.auth.adapter.port.in.web.filter.JwtAuthFilter;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final List<HeaderWriter> securityHeaderWriters;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          CorsConfigurationSource corsConfigurationSource,
                          List<HeaderWriter> securityHeaderWriters) {
        this.jwtAuthFilter           = jwtAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.securityHeaderWriters   = securityHeaderWriters;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // ── Security Headers (OWASP) ─────────────────────────────────
            .headers(h -> h
                .frameOptions(f -> f.deny())                          // Anti-Clickjacking
                .xssProtection(x -> x.disable())                      // CSP é suficiente
                .referrerPolicy(r -> r.policy(
                    org.springframework.security.web.header.writers
                        .ReferrerPolicyHeaderWriter
                        .ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
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
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
            )

            // ── Custom Error Responses ────────────────────────────────────
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType("application/json");
                    res.getWriter().write(
                        "{\"errorCode\":\"UNAUTHORIZED\",\"message\":\"Token inválido ou ausente\"}");
                })
            )

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