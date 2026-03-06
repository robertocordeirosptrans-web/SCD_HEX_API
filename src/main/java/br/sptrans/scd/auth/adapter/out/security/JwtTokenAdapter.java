package br.sptrans.scd.auth.adapter.out.security;



import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.port.out.TokenGeneratorPort;
import br.sptrans.scd.auth.domain.port.out.TokenValidatorPort;

/**
 * Adapter de saída: implementa TokenGeneratorPort usando JWT (auth0).
 * O domínio NÃO sabe que existe JWT — só conhece o port.
 */
@Component
public class JwtTokenAdapter implements TokenGeneratorPort, TokenValidatorPort {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-hours:2}")
    private int expirationHours;

    @Override
    public String generate(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
            .withIssuer("app-api")
            .withSubject(user.getCodLogin())
            .withClaim("userId", user.getIdUsuario())
            .withExpiresAt(Instant.now().plus(expirationHours, ChronoUnit.HOURS))
            .sign(algorithm);
    }

    @Override
    public String generateRefresh(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
            .withIssuer("app-api")
            .withSubject(user.getCodLogin())
            .withClaim("type", "refresh")
            .withExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .sign(algorithm);
    }

    @Override
    public String validateAndGetSubject(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("app-api")
                .build()
                .verify(token)
                .getSubject();
        } catch (com.auth0.jwt.exceptions.JWTVerificationException | IllegalArgumentException e) {
            return null;
        }
    }
}