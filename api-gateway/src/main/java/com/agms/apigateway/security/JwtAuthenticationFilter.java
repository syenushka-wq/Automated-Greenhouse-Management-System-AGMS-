package com.agms.apigateway.security;

import com.agms.apigateway.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtAuthenticationFilter
 * ========================
 * Servlet-based JWT filter — runs once per request via OncePerRequestFilter.
 *
 * IMPORTANT: This gateway uses spring-cloud-starter-gateway-server-webmvc
 * (Servlet stack, NOT WebFlux). So we use OncePerRequestFilter here,
 * NOT GatewayFilter / WebFilter.
 *
 * Flow:
 *   1. Check if path is public (no auth needed)
 *   2. Extract "Authorization: Bearer <token>" header
 *   3. Validate token with JwtUtil
 *   4. If valid   → pass to next filter in chain
 *   5. If invalid → write 401 JSON response and stop chain
 *
 * Registered in: SecurityConfig (addFilterBefore)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    // ── Paths that do NOT need a JWT token ──────────────────────────────────
    private static final String[] PUBLIC_PATHS = {
            "/actuator",
            "/actuator/health",
            "/actuator/info",
            "/fallback"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // ── Skip JWT check for public paths ─────────────────────────────────
        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Extract Authorization header ─────────────────────────────────────
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            log.warn("[Gateway] Missing Authorization header → {}", requestPath);
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED,
                    "Authorization header is missing");
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn("[Gateway] Authorization header must start with 'Bearer ' → {}", requestPath);
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED,
                    "Authorization header must start with 'Bearer '");
            return;
        }

        // ── Extract raw token (strip "Bearer " prefix) ───────────────────────
        String token = authHeader.substring(7);

        // ── Validate token ───────────────────────────────────────────────────
        try {
            jwtUtil.validateToken(token);

            // Forward authenticated username to downstream services as header
            String username = jwtUtil.extractUsername(token);
            request.setAttribute("X-Auth-User", username);
            log.debug("[Gateway] JWT valid for user '{}' → {}", username, requestPath);

            // Proceed to downstream
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            log.warn("[Gateway] JWT expired → {}", requestPath);
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token has expired");

        } catch (MalformedJwtException ex) {
            log.warn("[Gateway] JWT malformed → {}", requestPath);
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token is malformed");

        } catch (SignatureException ex) {
            log.warn("[Gateway] JWT signature invalid → {}", requestPath);
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT signature is invalid");

        } catch (UnsupportedJwtException ex) {
            log.warn("[Gateway] JWT unsupported → {}", requestPath);
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token is unsupported");

        } catch (IllegalArgumentException ex) {
            log.warn("[Gateway] JWT empty/null → {}", requestPath);
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token is empty");
        }
    }

    /**
     * Checks if the request path is in the public (no-auth) list.
     */
    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Writes a standardized JSON error response to the HTTP response.
     *
     * @param response HTTP response
     * @param status   HTTP status code
     * @param message  error message
     */
    private void writeErrorResponse(HttpServletResponse response,
                                    HttpStatus status,
                                    String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());

        objectMapper.writeValue(response.getWriter(), body);
    }
}