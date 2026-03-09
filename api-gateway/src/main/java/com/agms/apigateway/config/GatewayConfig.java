package com.agms.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * GatewayConfig
 * ==============
 * Route definitions for Spring Cloud Gateway MVC (Servlet-based, Spring Boot 4.x).
 *
 * NOTE: Spring Cloud Gateway MVC uses RouterFunction API (not RouteLocator).
 *       This is fundamentally different from the WebFlux-based gateway.
 *
 * Route Table:
 * ┌──────────────────────────┬──────────────────────────────────┬────────┐
 * │ Incoming Path            │ Downstream Service (Eureka)      │ Port   │
 * ├──────────────────────────┼──────────────────────────────────┼────────┤
 * │ /api/zones/**            │ ZONE-MANAGEMENT-SERVICE          │ 8081   │
 * │ /api/sensors/**          │ SENSOR-TELEMETRY-SERVICE         │ 8082   │
 * │ /api/automation/**       │ AUTOMATION-CONTROL-SERVICE       │ 8083   │
 * │ /api/crops/**            │ CROP-INVENTORY-SERVICE           │ 8084   │
 * └──────────────────────────┴──────────────────────────────────┴────────┘
 *
 * lb:// prefix → Eureka load-balanced lookup (no hardcoded IPs)
 * Circuit Breaker → falls back to /fallback/{service} if service is down
 */
@Configuration
public class GatewayConfig {

    // ── Route 1: Zone Management Service ────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> zoneRoute() {
        return GatewayRouterFunctions.route("zone-management-service")
                .route(path("/api/zones/**"),
                        HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("ZONE-MANAGEMENT-SERVICE"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "zoneCB",
                        URI.create("forward:/fallback/zone")))
                .build();
    }

    // ── Route 2: Sensor Telemetry Service ───────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> sensorRoute() {
        return GatewayRouterFunctions.route("sensor-telemetry-service")
                .route(path("/api/sensors/**"),
                        HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("SENSOR-TELEMETRY-SERVICE"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "sensorCB",
                        URI.create("forward:/fallback/sensor")))
                .build();
    }

    // ── Route 3: Automation & Control Service ───────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> automationRoute() {
        return GatewayRouterFunctions.route("automation-control-service")
                .route(path("/api/automation/**"),
                        HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("AUTOMATION-CONTROL-SERVICE"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "automationCB",
                        URI.create("forward:/fallback/automation")))
                .build();
    }

    // ── Route 4: Crop Inventory Service ─────────────────────────────────────
    @Bean
    public RouterFunction<ServerResponse> cropRoute() {
        return GatewayRouterFunctions.route("crop-inventory-service")
                .route(path("/api/crops/**"),
                        HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("CROP-INVENTORY-SERVICE"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "cropCB",
                        URI.create("forward:/fallback/crop")))
                .build();
    }
}