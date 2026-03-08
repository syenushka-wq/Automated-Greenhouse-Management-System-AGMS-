package com.agms.apigateway.controller;

import org.springframework.boot.autoconfigure.ssl.SslBundleProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@Slf4j
@CrossOrigin(origins = "*")
public class TestController {

    // ==================== PUBLIC ENDPOINTS (No Authentication Required) ====================

    /**
     * Public endpoint - No JWT required
     * Test the gateway is running
     */
    @GetMapping("/public/hello")
    public ResponseEntity<Map<String, Object>> publicHello() {
        log.info("Public hello endpoint accessed");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to AGMS API Gateway!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "UP");
        response.put("version", "1.0.0");

        return ResponseEntity.ok(response);
    }

    /**
     * Public endpoint - Health check
     */
    @GetMapping("/public/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "api-gateway");
        response.put("time", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Public endpoint - Get server info
     */
    @GetMapping("/public/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "AGMS API Gateway");
        info.put("version", "1.0.0");
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("os", System.getProperty("os.name"));
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("freeMemory", Runtime.getRuntime().freeMemory() / (1024 * 1024) + " MB");
        info.put("totalMemory", Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB");
        info.put("maxMemory", Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");
        info.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(info);
    }

    // ==================== SECURED ENDPOINTS (JWT Required) ====================

    /**
     * Secured endpoint - Requires valid JWT
     * Test authentication
     */
    @GetMapping("/secured/hello")
    public ResponseEntity<Map<String, Object>> securedHello(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        log.info("Secured hello endpoint accessed");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "You have accessed a secured endpoint!");
        response.put("timestamp", LocalDateTime.now());
        response.put("authHeader", authHeader != null ? "Present" : "Missing");

        return ResponseEntity.ok(response);
    }

    /**
     * Secured endpoint - Returns user info from JWT
     */
    @GetMapping("/secured/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", userId != null ? userId : "Not available");
        userInfo.put("username", username != null ? username : "Not available");
        userInfo.put("roles", roles != null ? roles : "Not available");
        userInfo.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(userInfo);
    }

    /**
     * Secured endpoint - Echo test with POST
     */
    @PostMapping("/secured/echo")
    public ResponseEntity<Map<String, Object>> echoPost(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-User-Name", required = false) String username) {

        Map<String, Object> response = new HashMap<>(payload);
        response.put("echoedBy", username != null ? username : "anonymous");
        response.put("echoedAt", LocalDateTime.now());
        response.put("requestId", UUID.randomUUID().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Secured endpoint - Test different HTTP methods
     */
    @PutMapping("/secured/resource/{id}")
    public ResponseEntity<Map<String, Object>> updateResource(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Resource updated successfully");
        response.put("id", id);
        response.put("updates", updates);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/secured/resource/{id}")
    public ResponseEntity<Map<String, Object>> deleteResource(@PathVariable String id) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Resource deleted successfully");
        response.put("id", id);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ==================== ROUTE TESTING ENDPOINTS ====================

    /**
     * Test routing to Zone Service
     */
    @GetMapping("/test-route/zone")
    public ResponseEntity<Map<String, String>> testZoneRoute() {
        return ResponseEntity.ok(Map.of(
                "message", "This request would be routed to Zone Service",
                "actualService", "api-gateway",
                "targetService", "zone-service",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Test routing to Sensor Service
     */
    @GetMapping("/test-route/sensor")
    public ResponseEntity<Map<String, String>> testSensorRoute() {
        return ResponseEntity.ok(Map.of(
                "message", "This request would be routed to Sensor Service",
                "actualService", "api-gateway",
                "targetService", "sensor-service",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Test routing to Automation Service
     */
    @GetMapping("/test-route/automation")
    public ResponseEntity<Map<String, String>> testAutomationRoute() {
        return ResponseEntity.ok(Map.of(
                "message", "This request would be routed to Automation Service",
                "actualService", "api-gateway",
                "targetService", "automation-service",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Test routing to Crop Service
     */
    @GetMapping("/test-route/crop")
    public ResponseEntity<Map<String, String>> testCropRoute() {
        return ResponseEntity.ok(Map.of(
                "message", "This request would be routed to Crop Service",
                "actualService", "api-gateway",
                "targetService", "crop-service",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== ERROR TESTING ENDPOINTS ====================

    /**
     * Test 400 Bad Request
     */
    @GetMapping("/error/bad-request")
    public ResponseEntity<Map<String, Object>> testBadRequest() {
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "error", "Bad Request",
                        "message", "This is a test bad request error",
                        "timestamp", LocalDateTime.now()
                ));
    }

    /**
     * Test 401 Unauthorized
     */
    @GetMapping("/error/unauthorized")
    public ResponseEntity<Map<String, Object>> testUnauthorized() {
        return ResponseEntity.status(401)
                .body(Map.of(
                        "error", "Unauthorized",
                        "message", "This is a test unauthorized error",
                        "timestamp", LocalDateTime.now()
                ));
    }

    /**
     * Test 403 Forbidden
     */
    @GetMapping("/error/forbidden")
    public ResponseEntity<Map<String, Object>> testForbidden() {
        return ResponseEntity.status(403)
                .body(Map.of(
                        "error", "Forbidden",
                        "message", "This is a test forbidden error",
                        "timestamp", LocalDateTime.now()
                ));
    }

    /**
     * Test 404 Not Found
     */
    @GetMapping("/error/not-found")
    public ResponseEntity<Map<String, Object>> testNotFound() {
        return ResponseEntity.status(404)
                .body(Map.of(
                        "error", "Not Found",
                        "message", "This is a test not found error",
                        "timestamp", LocalDateTime.now()
                ));
    }

    /**
     * Test 500 Internal Server Error
     */
    @GetMapping("/error/server-error")
    public ResponseEntity<Map<String, Object>> testServerError() {
        return ResponseEntity.status(500)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "message", "This is a test server error",
                        "timestamp", LocalDateTime.now()
                ));
    }

    // ==================== CORS TESTING ENDPOINTS ====================

    /**
     * Test CORS with GET
     */
    @GetMapping("/cors-test")
    public ResponseEntity<Map<String, Object>> corsGetTest(
            @RequestHeader(value = "Origin", required = false) String origin) {

        return ResponseEntity.ok(Map.of(
                "message", "CORS GET test successful",
                "method", "GET",
                "origin", origin != null ? origin : "No origin header",
                "timestamp", LocalDateTime.now()
        ));
    }

    /**
     * Test CORS with POST
     */
    @PostMapping("/cors-test")
    public ResponseEntity<Map<String, Object>> corsPostTest(
            @RequestBody(required = false) Map<String, Object> payload,
            @RequestHeader(value = "Origin", required = false) String origin) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS POST test successful");
        response.put("method", "POST");
        response.put("origin", origin != null ? origin : "No origin header");
        response.put("receivedPayload", payload);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * Test CORS with OPTIONS (handled automatically by Spring)
     */
//    @SslBundleProperties.Options("/cors-test")
//    public ResponseEntity<Void> corsOptionsTest() {
//        return ResponseEntity.ok().build();
//    }

    // ==================== LOAD TESTING ENDPOINTS ====================

    /**
     * Simulate delay for load testing
     */
    @GetMapping("/load-test/delay")
    public ResponseEntity<Map<String, Object>> testDelay(
            @RequestParam(defaultValue = "1000") int milliseconds) throws InterruptedException {

        long startTime = System.currentTimeMillis();
        Thread.sleep(milliseconds);
        long endTime = System.currentTimeMillis();

        return ResponseEntity.ok(Map.of(
                "message", "Delay test completed",
                "requestedDelay", milliseconds + "ms",
                "actualDelay", (endTime - startTime) + "ms",
                "timestamp", LocalDateTime.now()
        ));
    }

    /**
     * Simulate CPU load
     */
    @GetMapping("/load-test/cpu")
    public ResponseEntity<Map<String, Object>> testCpuLoad(
            @RequestParam(defaultValue = "1000000") int iterations) {

        long startTime = System.currentTimeMillis();

        // Perform CPU-intensive calculation
        double result = 0;
        for (int i = 0; i < iterations; i++) {
            result += Math.sqrt(i) * Math.sin(i);
        }

        long endTime = System.currentTimeMillis();

        return ResponseEntity.ok(Map.of(
                "message", "CPU load test completed",
                "iterations", iterations,
                "result", result,
                "timeTaken", (endTime - startTime) + "ms",
                "timestamp", LocalDateTime.now()
        ));
    }
}