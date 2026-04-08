package com.agms.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // Zone Service
                .route("zone-management-service", r -> r
                        .path("/api/zones/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("zoneCB")
                                .setFallbackUri("forward:/fallback/zone")))
                        .uri("lb://ZONE-MANAGEMENT-SERVICE"))

                // Sensor Service
                .route("sensor-telemetry-service", r -> r
                        .path("/api/sensors/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("sensorCB")
                                .setFallbackUri("forward:/fallback/sensor")))
                        .uri("lb://SENSOR-TELEMETRY-SERVICE"))

                // Automation Service
                .route("automation-control-service", r -> r
                        .path("/api/automation/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("automationCB")
                                .setFallbackUri("forward:/fallback/automation")))
                        .uri("lb://AUTOMATION-CONTROL-SERVICE"))

                // Crop Service
                .route("crop-inventory-service", r -> r
                        .path("/api/crops/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("cropCB")
                                .setFallbackUri("forward:/fallback/crop")))
                        .uri("lb://CROP-INVENTORY-SERVICE"))

                .build();
    }
}