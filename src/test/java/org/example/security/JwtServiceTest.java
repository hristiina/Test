package org.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-signing-key-must-be-at-least-32-bytes-long";

    private final JwtService jwtService = new JwtService(new JwtProperties(SECRET, 30));

    @Test
    void generateThenParseRoundTripRecoversSubjectAndRoles() {
        String token = jwtService.generateToken("alice", List.of("USER", "ADMIN"));

        Claims claims = jwtService.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("alice");
        assertThat(claims.get(JwtService.ROLES_CLAIM, List.class)).containsExactly("USER", "ADMIN");
    }

    @Test
    void expiredTokenThrowsExpiredJwtException() {
        JwtService expiringImmediately = new JwtService(new JwtProperties(SECRET, -1));
        String token = expiringImmediately.generateToken("alice", List.of("USER"));

        assertThatThrownBy(() -> expiringImmediately.parseClaims(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void tokenSignedWithDifferentKeyThrowsSignatureException() {
        JwtService otherKeyService = new JwtService(new JwtProperties("a-completely-different-32-byte-plus-secret-key", 30));
        String token = otherKeyService.generateToken("alice", List.of("USER"));

        assertThatThrownBy(() -> jwtService.parseClaims(token))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void expirationSecondsMatchesConfiguredMinutes() {
        assertThat(jwtService.getExpirationSeconds()).isEqualTo(30 * 60L);
    }
}
