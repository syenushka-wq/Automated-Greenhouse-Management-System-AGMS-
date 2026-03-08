package com.agms.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/zones")
    public ResponseEntity<Map<String, Object>> zoneFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Zone Service is currently unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now(),
                        "service", "zone-service"
                ));
    }

    @GetMapping("/sensors")
    public ResponseEntity<Map<String, Object>> sensorFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Sensor Service is currently unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now(),
                        "service", "sensor-service"
                ));
    }

    @GetMapping("/automation")
    public ResponseEntity<Map<String, Object>> automationFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Automation Service is currently unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now(),
                        "service", "automation-service"
                ));
    }

    @GetMapping("/crops")
    public ResponseEntity<Map<String, Object>> cropFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Crop Service is currently unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now(),
                        "service", "crop-service"
                ));
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Auth Service is currently unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now(),
                        "service", "auth-service"
                ));
    }
}