package com.ecommerce.authservice.service;

import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final JwtDecoder jwtDecoder;

    public AuthService(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public TokenIntrospection introspect(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return new TokenIntrospection(
                    true,
                    jwt.getSubject(),
                    resolveAuthorities(jwt),
                    jwt.getExpiresAt()
            );
        } catch (JwtException ex) {
            return new TokenIntrospection(false, null, null, null);
        }
    }

    private String resolveAuthorities(Jwt jwt) {
        Object scope = jwt.getClaims().get("scope");
        if (scope instanceof String scopeString && !scopeString.isBlank()) {
            return scopeString;
        }

        Object realmAccess = jwt.getClaims().get("realm_access");
        if (realmAccess instanceof Map<?, ?> map) {
            Object roles = map.get("roles");
            if (roles instanceof Collection<?> roleList) {
                return roleList.stream().map(String::valueOf).collect(Collectors.joining(" "));
            }
        }

        return "";
    }

    public record TokenIntrospection(boolean active, String subject, String role, Instant expiresAt) {
    }
}

