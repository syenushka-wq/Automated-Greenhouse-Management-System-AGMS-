package com.agms.apigateway.security;

import com.agms.apigateway.util.TokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenValidator tokenValidator;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/refresh",
            "/actuator/health",
            "/actuator/info",
            "/eureka",
            "/api/test/public"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip authentication for public paths
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        // Validate token using TokenValidator
        TokenValidator.ValidationResult result = tokenValidator.validateToken(token);

        if (!result.isValid()) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, result.getMessage());
            return;
        }

        // Get token info
        TokenValidator.TokenInfo tokenInfo = result.getTokenInfo();

        // Set authentication in SecurityContext
        List<SimpleGrantedAuthority> authorities = tokenInfo.getRoles() != null
                ? tokenInfo.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
                : Collections.emptyList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(tokenInfo.getUsername(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Add user info to request attributes for downstream services
        request.setAttribute("X-User-Id", tokenInfo.getUserId());
        request.setAttribute("X-User-Name", tokenInfo.getUsername());
        request.setAttribute("X-User-Roles", tokenInfo.getRoles());

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"error\": \"%s\", \"message\": \"%s\", \"timestamp\": \"%s\"}",
                status == 401 ? "Unauthorized" : "Forbidden",
                message,
                new java.util.Date()
        ));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") ||
                path.startsWith("/actuator/") ||
                path.startsWith("/eureka/") ||
                path.startsWith("/api/test/public");
    }
}