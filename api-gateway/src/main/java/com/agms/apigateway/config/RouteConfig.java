package com.agms.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.Duration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Zone Service Routes
                .route("zone-service", r -> r
                        .path("/api/zones/**")
                        .and().method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("zoneServiceBreaker")
                                        .setFallbackUri("forward:/fallback/zones"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.SERVICE_UNAVAILABLE)
                                        .setMethods(HttpMethod.GET, HttpMethod.POST)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .addRequestHeader("X-Request-Start", String.valueOf(System.currentTimeMillis()))
                                .addResponseHeader("X-Service-Name", "zone-service")
                                .removeRequestHeader("Cookie"))
                        .uri("lb://ZONE-SERVICE"))

                // Sensor Service Routes
                .route("sensor-service", r -> r
                        .path("/api/sensors/**")
                        .and().method(HttpMethod.GET)
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("sensorServiceBreaker")
                                        .setFallbackUri("forward:/fallback/sensors"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.SERVICE_UNAVAILABLE)
                                        .setMethods(HttpMethod.GET))
                                .addResponseHeader("X-Service-Name", "sensor-service")
                                .removeRequestHeader("Cookie"))
                        .uri("lb://SENSOR-SERVICE"))

                // Automation Service Routes
                .route("automation-service", r -> r
                        .path("/api/automation/**")
                        .and().method(HttpMethod.GET, HttpMethod.POST)
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("automationServiceBreaker")
                                        .setFallbackUri("forward:/fallback/automation"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.SERVICE_UNAVAILABLE)
                                        .setMethods(HttpMethod.GET, HttpMethod.POST))
                                .addResponseHeader("X-Service-Name", "automation-service")
                                .removeRequestHeader("Cookie"))
                        .uri("lb://AUTOMATION-SERVICE"))

                // Crop Service Routes
                .route("crop-service", r -> r
                        .path("/api/crops/**")
                        .and().method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT)
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("cropServiceBreaker")
                                        .setFallbackUri("forward:/fallback/crops"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.SERVICE_UNAVAILABLE)
                                        .setMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT))
                                .addResponseHeader("X-Service-Name", "crop-service")
                                .removeRequestHeader("Cookie"))
                        .uri("lb://CROP-SERVICE"))

                // Auth Service Routes (Public)
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .and().method(HttpMethod.POST)
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("authServiceBreaker")
                                        .setFallbackUri("forward:/fallback/auth"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.SERVICE_UNAVAILABLE)
                                        .setMethods(HttpMethod.POST))
                                .removeRequestHeader("Cookie")
                                .removeRequestHeader("Authorization")
                                .addResponseHeader("X-Service-Name", "auth-service"))
                        .uri("lb://AUTH-SERVICE"))

                // Eureka Dashboard Route
                .route("eureka-dashboard", r -> r
                        .path("/eureka/**")
                        .filters(f -> f
                                .setPath("/")
                                .removeRequestHeader("Cookie"))
                        .uri("http://localhost:8761"))

                // Test Public Routes
                .route("test-public", r -> r
                        .path("/api/test/public/**")
                        .filters(f -> f
                                .addResponseHeader("X-Service-Name", "test-public"))
                        .uri("lb://API-GATEWAY"))

                // Test Secured Routes
                .route("test-secured", r -> r
                        .path("/api/test/secured/**")
                        .filters(f -> f
                                .addResponseHeader("X-Service-Name", "test-secured"))
                        .uri("lb://API-GATEWAY"))

                .build();
    }
}