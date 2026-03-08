package com.agms.apigateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TokenValidator {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private Long expiration;

    // Cache for blacklisted tokens (in production, use Redis)
    private final Map<String, Date> tokenBlacklist = new ConcurrentHashMap<>();

    /**
     * Get signing key from secret
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validate JWT token
     * @param token JWT token to validate
     * @return ValidationResult with status and details
     */
    public ValidationResult validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return ValidationResult.invalid("Token is null or empty");
        }

        // Check if token is blacklisted
        if (isTokenBlacklisted(token)) {
            return ValidationResult.invalid("Token has been revoked");
        }

        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            Claims claims = jws.getBody();

            // Check expiration
            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                return ValidationResult.invalid("Token has expired");
            }

            // Extract user information
            String username = claims.getSubject();
            String userId = claims.get("userId", String.class);
            List<String> roles = claims.get("roles", List.class);

            if (username == null || username.trim().isEmpty()) {
                return ValidationResult.invalid("Token missing username");
            }

            // Token is valid
            return ValidationResult.valid(
                    TokenInfo.builder()
                            .username(username)
                            .userId(userId)
                            .roles(roles)
                            .issuedAt(claims.getIssuedAt())
                            .expiration(expiration)
                            .build()
            );

        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return ValidationResult.invalid("Token has expired");
        } catch (SignatureException e) {
            log.warn("Invalid token signature: {}", e.getMessage());
            return ValidationResult.invalid("Invalid token signature");
        } catch (MalformedJwtException e) {
            log.warn("Malformed token: {}", e.getMessage());
            return ValidationResult.invalid("Malformed token");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported token: {}", e.getMessage());
            return ValidationResult.invalid("Unsupported token");
        } catch (IllegalArgumentException e) {
            log.warn("Illegal argument: {}", e.getMessage());
            return ValidationResult.invalid("Invalid token format");
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return ValidationResult.invalid("Token validation failed");
        }
    }

    /**
     * Validate token and return claims if valid
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to parse claims: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Extract user ID from token
     */
    public String extractUserId(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.get("userId", String.class) : null;
    }

    /**
     * Extract roles from token
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.get("roles", List.class) : null;
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims == null || claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get token expiration date
     */
    public Date getExpirationDate(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
     * Blacklist a token (logout)
     */
    public void blacklistToken(String token) {
        Claims claims = getClaims(token);
        if (claims != null) {
            tokenBlacklist.put(token, claims.getExpiration());
            log.info("Token blacklisted for user: {}", claims.getSubject());

            // Clean up expired tokens from blacklist
            cleanupBlacklist();
        }
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        Date blacklistExpiry = tokenBlacklist.get(token);
        if (blacklistExpiry != null) {
            // Remove from blacklist if expired
            if (blacklistExpiry.before(new Date())) {
                tokenBlacklist.remove(token);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Clean up expired tokens from blacklist
     */
    private void cleanupBlacklist() {
        Date now = new Date();
        tokenBlacklist.entrySet().removeIf(entry -> entry.getValue().before(now));
    }

    /**
     * Get blacklist size
     */
    public int getBlacklistSize() {
        return tokenBlacklist.size();
    }

    /**
     * Clear entire blacklist
     */
    public void clearBlacklist() {
        tokenBlacklist.clear();
        log.info("Token blacklist cleared");
    }

    /**
     * Inner class for validation result
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final TokenInfo tokenInfo;

        private ValidationResult(boolean valid, String message, TokenInfo tokenInfo) {
            this.valid = valid;
            this.message = message;
            this.tokenInfo = tokenInfo;
        }

        public static ValidationResult valid(TokenInfo tokenInfo) {
            return new ValidationResult(true, "Token is valid", tokenInfo);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message, null);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        public TokenInfo getTokenInfo() {
            return tokenInfo;
        }

        @Override
        public String toString() {
            return "ValidationResult{" +
                    "valid=" + valid +
                    ", message='" + message + '\'' +
                    ", tokenInfo=" + tokenInfo +
                    '}';
        }
    }

    /**
     * Token information class
     */
    public static class TokenInfo {
        private final String username;
        private final String userId;
        private final List<String> roles;
        private final Date issuedAt;
        private final Date expiration;

        private TokenInfo(Builder builder) {
            this.username = builder.username;
            this.userId = builder.userId;
            this.roles = builder.roles;
            this.issuedAt = builder.issuedAt;
            this.expiration = builder.expiration;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getUsername() {
            return username;
        }

        public String getUserId() {
            return userId;
        }

        public List<String> getRoles() {
            return roles;
        }

        public Date getIssuedAt() {
            return issuedAt;
        }

        public Date getExpiration() {
            return expiration;
        }

        @Override
        public String toString() {
            return "TokenInfo{" +
                    "username='" + username + '\'' +
                    ", userId='" + userId + '\'' +
                    ", roles=" + roles +
                    ", issuedAt=" + issuedAt +
                    ", expiration=" + expiration +
                    '}';
        }

        public static class Builder {
            private String username;
            private String userId;
            private List<String> roles;
            private Date issuedAt;
            private Date expiration;

            public Builder username(String username) {
                this.username = username;
                return this;
            }

            public Builder userId(String userId) {
                this.userId = userId;
                return this;
            }

            public Builder roles(List<String> roles) {
                this.roles = roles;
                return this;
            }

            public Builder issuedAt(Date issuedAt) {
                this.issuedAt = issuedAt;
                return this;
            }

            public Builder expiration(Date expiration) {
                this.expiration = expiration;
                return this;
            }

            public TokenInfo build() {
                return new TokenInfo(this);
            }
        }
    }
}