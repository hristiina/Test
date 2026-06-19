package org.example.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Issues signed JWTs for successfully authenticated users.
 *
 * <p>This service only issues tokens; validating incoming bearer tokens on
 * protected endpoints is a separate, not-yet-implemented concern.
 */
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, Collection<String> roleNames) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMinutes(jwtProperties.expirationMinutes()));

        return Jwts.builder()
                .subject(username)
                .claim("roles", List.copyOf(roleNames))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public long getExpirationSeconds() {
        return Duration.ofMinutes(jwtProperties.expirationMinutes()).toSeconds();
    }
}
