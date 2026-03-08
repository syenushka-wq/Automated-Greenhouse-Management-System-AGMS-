package com.agms.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;               // ❌ missing
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder; // ❌ missing
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("zone-service", r -> r.path("/api/zones/**")
                        .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://ZONE-SERVICE"))

                .route("sensor-service", r -> r.path("/api/sensors/**")
                        .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://SENSOR-TELEMETRY-SERVICE"))

                .route("automation-service", r -> r.path("/api/automation/**")
                        .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://AUTOMATION-SERVICE"))

                .route("crop-service", r -> r.path("/api/crops/**")
                        .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://CROP-INVENTORY-SERVICE"))

                .build();
    }
}