package com.agms.zoneservice.service;

import com.agms.zoneservice.Entity.Zone;
import com.agms.zoneservice.Repository.ZoneRepository;
import com.agms.zoneservice.client.SensorClient;
import com.agms.zoneservice.dto.DeviceRequest;
import com.agms.zoneservice.dto.ZoneRequest;

import org.springframework.stereotype.Service;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final SensorClient sensorClient;

    public ZoneService(ZoneRepository zoneRepository, SensorClient sensorClient) {
        this.zoneRepository = zoneRepository;
        this.sensorClient = sensorClient;
    }

    public Zone createZone(ZoneRequest request) {

        if(request.getMinTemp() >= request.getMaxTemp()){
            throw new RuntimeException("Min temp must be less than max temp");
        }

        Zone zone = new Zone();
        zone.setName(request.getName());
        zone.setMinTemp(request.getMinTemp());
        zone.setMaxTemp(request.getMaxTemp());

        Zone savedZone = zoneRepository.save(zone);

        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setName("Temp-Sensor");
        deviceRequest.setZoneId(savedZone.getId().toString());

        String deviceId = sensorClient.registerDevice(deviceRequest);

        savedZone.setDeviceId(deviceId);

        return zoneRepository.save(savedZone);
    }
}