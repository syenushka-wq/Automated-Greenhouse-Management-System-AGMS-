package com.agms.zoneservice.controller;

import com.agms.zoneservice.service.ZoneDTO;
import com.agms.zoneservice.service.ZoneService;
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


    private final ZoneService zoneService;

    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(
            @Valid @RequestBody ZoneDTO zoneDTO,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        // Extract token from header (remove "Bearer " prefix if present)
        String actualToken = token != null && token.startsWith("Bearer ") ?
                token.substring(7) : token;

        if (userId != null) {
            zoneDTO.setUserId(userId);
        }

        ZoneDTO createdZone = zoneService.createZone(zoneDTO, actualToken);
        return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZone(@PathVariable String id) {
        ZoneDTO zone = zoneService.getZone(id);
        return ResponseEntity.ok(zone);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<ZoneDTO> getZoneByDeviceId(@PathVariable String deviceId) {
        ZoneDTO zone = zoneService.getZoneByDeviceId(deviceId);
        return ResponseEntity.ok(zone);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(
            @PathVariable String id,
            @Valid @RequestBody ZoneDTO zoneDTO) {
        ZoneDTO updatedZone = zoneService.updateZone(id, zoneDTO);
        return ResponseEntity.ok(updatedZone);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable String id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ZoneDTO>> getAllZones() {
        List<ZoneDTO> zones = zoneService.getAllZones();
        return ResponseEntity.ok(zones);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ZoneDTO>> getZonesByUser(@PathVariable String userId) {
        List<ZoneDTO> zones = zoneService.getZonesByUser(userId);
        return ResponseEntity.ok(zones);
    }
}