package com.agms.zoneservice.service;

import com.agms.zoneservice.Repository.ZoneRepository;
import com.agms.zoneservice.dto.ZoneDTO;
import com.agms.zoneservice.model.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneDTO createZone(ZoneDTO dto) {

        Zone zone = Zone.builder()
                .name(dto.getName())
                .deviceId(dto.getDeviceId())
                .userId(dto.getUserId())
                .build();

        Zone saved = zoneRepository.save(zone);

        dto.setId(saved.getId());

        return dto;
    }

    public ZoneDTO getZone(String id) {

        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        return mapToDTO(zone);
    }

    public ZoneDTO getZoneByDeviceId(String deviceId) {

        Zone zone = zoneRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        return mapToDTO(zone);
    }

    public List<ZoneDTO> getAllZones() {

        return zoneRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ZoneDTO> getZonesByUser(String userId) {

        return zoneRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ZoneDTO updateZone(String id, ZoneDTO dto) {

        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        zone.setName(dto.getName());
        zone.setDeviceId(dto.getDeviceId());
        zone.setUserId(dto.getUserId());

        Zone updated = zoneRepository.save(zone);

        return mapToDTO(updated);
    }

    public void deleteZone(String id) {

        if (!zoneRepository.existsById(id)) {
            throw new RuntimeException("Zone not found");
        }

        zoneRepository.deleteById(id);
    }

    private ZoneDTO mapToDTO(Zone zone) {

        ZoneDTO dto = new ZoneDTO();

        dto.setId(zone.getId());
        dto.setName(zone.getName());
        dto.setDeviceId(zone.getDeviceId());
        dto.setUserId(zone.getUserId());

        return dto;
    }

}