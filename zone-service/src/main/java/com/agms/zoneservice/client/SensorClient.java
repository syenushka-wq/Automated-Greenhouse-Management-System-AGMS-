package com.agms.zoneservice.client;

import com.agms.zoneservice.dto.DeviceRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sensor-service", url = "")
public interface SensorClient {

    @PostMapping("/api/devices")
    String registerDevice(@RequestBody DeviceRequest request);

}