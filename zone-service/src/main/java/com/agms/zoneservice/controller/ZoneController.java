package com.agms.zoneservice.controller;

import com.agms.zoneservice.service.ZoneDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final com.agms.zone.service.ZoneService zoneService;

    // -------------------------
    // Test API
    // -------------------------
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Zone Service is running");
    }

    // -------------------------
    // Create Zone
    // -------------------------
    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(
            @Valid @RequestBody ZoneDTO zoneDTO,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        String actualToken = extractToken(token);

        if (userId != null) {
            zoneDTO.setUserId(userId);
        }

        ZoneDTO createdZone = zoneService.createZone(zoneDTO, actualToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdZone);
    }

    // -------------------------
    // Get Zone By ID
    // -------------------------
    @GetMapping("/id/{id}")
    public ResponseEntity<ZoneDTO> getZone(@PathVariable String id) {

        ZoneDTO zone = zoneService.getZone(id);

        return ResponseEntity.ok(zone);
    }

    // -------------------------
    // Get Zone By Device ID
    // -------------------------
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<ZoneDTO> getZoneByDeviceId(@PathVariable String deviceId) {

        ZoneDTO zone = zoneService.getZoneByDeviceId(deviceId);

        return ResponseEntity.ok(zone);
    }

    // -------------------------
    // Update Zone
    // -------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(
            @PathVariable String id,
            @Valid @RequestBody ZoneDTO zoneDTO) {

        ZoneDTO updatedZone = zoneService.updateZone(id, zoneDTO);

        return ResponseEntity.ok(updatedZone);
    }

    // -------------------------
    // Delete Zone
    // -------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable String id) {

        zoneService.deleteZone(id);

        return ResponseEntity.noContent().build();
    }

    // -------------------------
    // Get All Zones
    // -------------------------
    @GetMapping
    public ResponseEntity<List<ZoneDTO>> getAllZones() {

        List<ZoneDTO> zones = zoneService.getAllZones();

        return ResponseEntity.ok(zones);
    }

    // -------------------------
    // Get Zones By User
    // -------------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ZoneDTO>> getZonesByUser(@PathVariable String userId) {

        List<ZoneDTO> zones = zoneService.getZonesByUser(userId);

        return ResponseEntity.ok(zones);
    }

    // -------------------------
    // Helper Method
    // -------------------------
    private String extractToken(String token) {

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return token;
    }
}