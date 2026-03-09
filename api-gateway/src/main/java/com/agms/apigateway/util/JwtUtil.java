package com.agms.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtUtil
 * ========
 * JWT token utility class — compatible with jjwt 0.12.6 API.
 *
 * NOTE: jjwt 0.12.x changed the API vs 0.11.x:
 *   - Use Jwts.parser()           (not parserBuilder())
 *   - Use .verifyWith(key)        (not .setSigningKey())
 *   - Use .parseSignedClaims()    (not .parseClaimsJws())
 *
 * Used by: JwtAuthenticationFilter → Spring Security filter chain
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Validates a JWT token string.
     * Throws runtime exceptions on failure — caught by JwtAuthenticationFilter.
     *
     * @param token raw token string (without "Bearer " prefix)
     */
    public void validateToken(final String token) {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }

    /**
     * Extracts all claims from a valid JWT token.
     *
     * @param token raw token string
     * @return Claims payload
     */
    public Claims extractAllClaims(final String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the subject (username) from the token.
     *
     * @param token raw token string
     * @return username
     */
    public String extractUsername(final String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Checks whether the token is expired.
     *
     * @param token raw token string
     * @return true if token expiry date is before now
     */
    public boolean isTokenExpired(final String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Builds a SecretKey from the configured jwt.secret property.
     * Uses HMAC-SHA algorithm.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}