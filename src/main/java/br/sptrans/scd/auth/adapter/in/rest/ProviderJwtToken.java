package br.sptrans.scd.auth.adapter.in.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// ══════════════════════════════════════════════════════════════════════════════
// ProvedorTokenJwt — geração e validação de tokens JWT
// ══════════════════════════════════════════════════════════════════════════════
@Component
public class ProviderJwtToken {

    @Value("${scd.jwt.secret:SCD-SPTRANS-key-secret-minimo-32-chars}")
    private String secret;

    @Value("${scd.jwt.expiracao-ms:28800000}") // 8 horas padrão
    private long expiredMs;

    public String gerarToken(Long idUsuario, String codLogin) {
        return Jwts.builder()
                .subject(codLogin)
                .claim("idUsuario", idUsuario)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(chave())
                .compact();
    }

    public Claims validarEExtrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey chave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

}

// ══════════════════════════════════════════════════════════════════════════════
// AuthenticationJwtFilter — valida Bearer token em cada requisição
// ══════════════════════════════════════════════════════════════════════════════
@Component
class AuthenticationJwtFilter extends OncePerRequestFilter {

    private final ProviderJwtToken provedorToken;

    AuthenticationJwtFilter(ProviderJwtToken provedorToken) {
        this.provedorToken = provedorToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");

        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            String token = cabecalho.substring(7);
            try {
                Claims claims = provedorToken.validarEExtrairClaims(token);

                @SuppressWarnings("unchecked")
                List<String> permissoes = (List<String>) claims.get("permissoes");

                List<SimpleGrantedAuthority> authorities = permissoes == null
                        ? List.of()
                        : permissoes.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth
                        = new UsernamePasswordAuthenticationToken(
                                claims.getSubject(), null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException ex) {
                // Token inválido ou expirado — deixa prosseguir sem autenticação
                // O Spring Security bloqueará na rota protegida
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Rotas públicas: login e recuperação de senha não exigem token
        return path.startsWith("/api/v1/auth/");
    }

}
