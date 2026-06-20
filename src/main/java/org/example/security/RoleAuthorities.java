package org.example.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Centralizes the {@code ROLE_} prefix convention shared between token issuance
 * ({@link JwtService}), token validation ({@link JwtAuthenticationFilter}), and
 * DB-backed authority loading ({@link CustomUserDetailsService}) so the three
 * sites can't silently desync.
 */
public final class RoleAuthorities {

    private static final String ROLE_PREFIX = "ROLE_";

    private RoleAuthorities() {
    }

    public static String stripPrefix(String authority) {
        return authority.replaceFirst("^" + ROLE_PREFIX, "");
    }

    public static GrantedAuthority toAuthority(String roleName) {
        return new SimpleGrantedAuthority(ROLE_PREFIX + roleName);
    }

    public static List<GrantedAuthority> toAuthorities(Collection<String> roleNames) {
        return roleNames.stream().map(RoleAuthorities::toAuthority).toList();
    }
}
