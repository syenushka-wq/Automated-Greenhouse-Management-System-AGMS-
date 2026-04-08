package com.agms.apigateway.security;

import com.agms.apigateway.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    private static final String[] PUBLIC_PATHS = {
            "/actuator",
            "/actuator/health",
            "/actuator/info",
            "/fallback"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getURI().getPath();

        // Public endpoints → skip authentication
        if (isPublicPath(requestPath)) {
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            log.warn("[Gateway] Missing Authorization header → {}", requestPath);
            return writeErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED,
                    "Authorization header is missing");
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn("[Gateway] Authorization header must start with 'Bearer ' → {}", requestPath);
            return writeErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED,
                    "Authorization header must start with 'Bearer '");
        }

        String token = authHeader.substring(7);

        // Validate JWT
        try {
            jwtUtil.validateToken(token);
            String username = jwtUtil.extractUsername(token);

            log.debug("[Gateway] JWT valid for user '{}' → {}", username, requestPath);

            // Pass username downstream via request header
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-Auth-User", username)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException ex) {
            return writeErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "JWT token has expired");
        } catch (MalformedJwtException ex) {
            return writeErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "JWT token is malformed");
        } catch (SignatureException ex) {
            return writeErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "JWT signature is invalid");
        } catch (UnsupportedJwtException ex) {
            return writeErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "JWT token is unsupported");
        } catch (IllegalArgumentException ex) {
            return writeErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "JWT token is empty");
        }
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) return true;
        }
        return false;
    }

    private Mono<Void> writeErrorResponse(ServerHttpResponse response,
                                          HttpStatus status,
                                          String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                status.value(), status.getReasonPhrase(), message, LocalDateTime.now()
        );

        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}