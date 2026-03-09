package com.agms.apigateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * FallbackController
 * ===================
 * Handles Circuit Breaker fallback responses when a downstream microservice
 * is unavailable or times out.
 *
 * Returns HTTP 503 Service Unavailable with a descriptive JSON body
 * so the frontend / caller gets a clear message instead of a raw error.
 *
 * Triggered by: GatewayConfig circuit breaker filters
 *               → forward:/fallback/{service}
 */
@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    // ── Zone Management Service fallback ────────────────────────────────────
    @GetMapping("/zone")
    @PostMapping("/zone")
    public ResponseEntity<Map<String, Object>> zoneFallback() {
        log.error("[Fallback] Zone Management Service unavailable");
        return buildFallbackResponse("Zone Management Service", "/api/zones");
    }

    // ── Sensor Telemetry Service fallback ───────────────────────────────────
    @GetMapping("/sensor")
    @PostMapping("/sensor")
    public ResponseEntity<Map<String, Object>> sensorFallback() {
        log.error("[Fallback] Sensor Telemetry Service unavailable");
        return buildFallbackResponse("Sensor Telemetry Service", "/api/sensors");
    }

    // ── Automation & Control Service fallback ───────────────────────────────
    @GetMapping("/automation")
    @PostMapping("/automation")
    public ResponseEntity<Map<String, Object>> automationFallback() {
        log.error("[Fallback] Automation & Control Service unavailable");
        return buildFallbackResponse("Automation & Control Service", "/api/automation");
    }

    // ── Crop Inventory Service fallback ─────────────────────────────────────
    @GetMapping("/crop")
    @PostMapping("/crop")
    public ResponseEntity<Map<String, Object>> cropFallback() {
        log.error("[Fallback] Crop Inventory Service unavailable");
        return buildFallbackResponse("Crop Inventory Service", "/api/crops");
    }

    // ── Helper ──────────────────────────────────────────────────────────────
    private ResponseEntity<Map<String, Object>> buildFallbackResponse(
            String serviceName, String path) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", 503,
                        "error", "Service Unavailable",
                        "message", serviceName + " is currently unavailable. Please try again later.",
                        "path", path,
                        "timestamp", LocalDateTime.now().toString()
                ));
    }
}