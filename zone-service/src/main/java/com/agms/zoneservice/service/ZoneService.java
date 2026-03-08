package com.agms.zoneservice.service;

import com.agms.zoneservice.Entity.Zone;
import com.agms.zoneservice.Repository.ZoneRepository;
import com.agms.zoneservice.client.IoTIntegrationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final IoTIntegrationClient iotClient;

    public ZoneDTO createZone(ZoneDTO zoneDTO, String token) {
        if (zoneDTO.getMinTemp() >= zoneDTO.getMaxTemp()) {
            throw new IllegalArgumentException("Minimum temperature must be less than maximum temperature");
        }

        Map<String, String> deviceRequest = new HashMap<>();
        deviceRequest.put("name", zoneDTO.getName() + "-Sensor");
        deviceRequest.put("zoneId", zoneDTO.getName());

        log.info("Registering device with IoT service...");
        Map<String, Object> deviceResponse = iotClient.registerDevice(
                "Bearer " + token,
                deviceRequest
        );

        Zone zone = new Zone();
        BeanUtils.copyProperties(zoneDTO, zone);
        zone.setId(null); // MongoDB auto-generates _id
        zone.setDeviceId((String) deviceResponse.get("deviceId"));
        zone.setCreatedAt(LocalDateTime.now());
        zone.setUpdatedAt(LocalDateTime.now());

        Zone savedZone = zoneRepository.save(zone);
        log.info("Zone created with ID: {}", savedZone.getId());

        ZoneDTO response = new ZoneDTO();
        BeanUtils.copyProperties(savedZone, response);
        return response;
    }

    public ZoneDTO getZone(String id) {
        Zone zone = zoneRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + id));
        ZoneDTO dto = new ZoneDTO();
        BeanUtils.copyProperties(zone, dto);
        return dto;
    }

    public ZoneDTO getZoneByDeviceId(String deviceId) {
        Zone zone = zoneRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Zone not found for device: " + deviceId));
        ZoneDTO dto = new ZoneDTO();
        BeanUtils.copyProperties(zone, dto);
        return dto;
    }

    public ZoneDTO updateZone(String id, ZoneDTO zoneDTO) {
        Zone zone = zoneRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + id));

        if (zoneDTO.getName() != null) zone.setName(zoneDTO.getName());
        if (zoneDTO.getType() != null) zone.setType(Boolean.parseBoolean(zoneDTO.getType()));
        if (zoneDTO.getMinTemp() != null) zone.setMinTemp(zoneDTO.getMinTemp());
        if (zoneDTO.getMaxTemp() != null) zone.setMaxTemp(zoneDTO.getMaxTemp());

        if (zone.getMinTemp() >= zone.getMaxTemp()) {
            throw new IllegalArgumentException("Minimum temperature must be less than maximum temperature");
        }

        zone.setUpdatedAt(LocalDateTime.now());

        Zone updatedZone = zoneRepository.save(zone);
        ZoneDTO response = new ZoneDTO();
        BeanUtils.copyProperties(updatedZone, response);
        return response;
    }

    public void deleteZone(String id) {
        if (!zoneRepository.existsById(id)) {
            throw new RuntimeException("Zone not found with id: " + id);
        }
        zoneRepository.deleteById(Long.valueOf(id));
        log.info("Zone deleted with ID: {}", id);
    }

    public List<ZoneDTO> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(zone -> {
                    ZoneDTO dto = new ZoneDTO();
                    BeanUtils.copyProperties(zone, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ZoneDTO> getZonesByUser(String userId) {
        return zoneRepository.findByUserId(userId).stream()
                .map(zone -> {
                    ZoneDTO dto = new ZoneDTO();
                    BeanUtils.copyProperties(zone, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}