package br.sptrans.scd.auth.adapter.port.out.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

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
// AuthenticationJwtFilter — DESATIVADO
// Este filtro foi desabilitado porque conflitava com o JwtAuthFilter
// registrado na SecurityConfig. Ambos tentavam validar o token JWT,
// mas com secrets diferentes (scd.jwt.secret vs api.security.token.secret),
// fazendo com que este filtro limpasse o SecurityContext após o JwtAuthFilter
// já tê-lo preenchido corretamente.
// ══════════════════════════════════════════════════════════════════════════════
