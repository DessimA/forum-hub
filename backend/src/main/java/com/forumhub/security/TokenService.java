package com.forumhub.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.forumhub.modules.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API ForumHub")
                    .withSubject(user.getEmail())
                    .withClaim("id", user.getId())
                    .withClaim("role", user.getProfile().getName())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("error generating jwt token", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("API ForumHub")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Invalid or expired JWT Token!");
        }
    }

    private Instant expirationDate() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }
}