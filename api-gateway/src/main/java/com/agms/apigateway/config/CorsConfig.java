package com.agms.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:8081",
            "http://localhost:8082",
            "http://localhost:8083",
            "http://localhost:8084",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:8080"
    );

    private static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
    );

    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "Origin",
            "Content-Type",
            "Accept",
            "Authorization",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-User-Id",
            "X-User-Name"
    );

    private static final List<String> EXPOSED_HEADERS = Arrays.asList(
            "Authorization",
            "X-User-Id",
            "X-User-Name"
    );

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow specific origins
        ALLOWED_ORIGINS.forEach(config::addAllowedOrigin);

        // Allow all methods
        ALLOWED_METHODS.forEach(config::addAllowedMethod);

        // Allow all headers
        ALLOWED_HEADERS.forEach(config::addAllowedHeader);

        // Expose headers to client
        EXPOSED_HEADERS.forEach(config::addExposedHeader);

        // Allow credentials
        config.setAllowCredentials(true);

        // Max age of pre-flight request cache
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}