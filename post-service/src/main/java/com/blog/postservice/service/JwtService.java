package com.blog.postservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Extract role e.g. "ROLE_WRITER"
    public String extractRole(String token) {
        Claims claims = getClaims(token);
        // authorities stored as a list — get first one
        Object auth = claims.get("authorities");
        if (auth == null) {
            // fallback: stored directly
            return claims.get("role", String.class);
        }
        return auth.toString().replace("[", "").replace("]", "").trim();
    }

    // Extract userId stored in token claims
    public Long extractUserId(String token) {
        Object id = getClaims(token).get("userId");
        return id != null ? Long.valueOf(id.toString()) : null;
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}