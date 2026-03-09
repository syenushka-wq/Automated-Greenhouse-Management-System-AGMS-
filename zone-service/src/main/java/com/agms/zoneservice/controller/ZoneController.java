package com.agms.zoneservice.controller;

import com.agms.zoneservice.dto.ZoneDTO;
import com.agms.zoneservice.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping("/test")
    public String test() {
        return "Zone Service Running";
    }

    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(
            @Valid @RequestBody ZoneDTO dto) {

        return ResponseEntity.ok(zoneService.createZone(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZone(@PathVariable String id) {

        return ResponseEntity.ok(zoneService.getZone(id));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<ZoneDTO> getZoneByDeviceId(@PathVariable String deviceId) {

        return ResponseEntity.ok(zoneService.getZoneByDeviceId(deviceId));
    }

    @GetMapping
    public ResponseEntity<List<ZoneDTO>> getAllZones() {

        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ZoneDTO>> getZonesByUser(@PathVariable String userId) {

        return ResponseEntity.ok(zoneService.getZonesByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(
            @PathVariable String id,
            @RequestBody ZoneDTO dto) {

        return ResponseEntity.ok(zoneService.updateZone(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable String id) {

        zoneService.deleteZone(id);

        return ResponseEntity.noContent().build();
    }

}