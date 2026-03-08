package com.agms.zoneservice;

import com.agms.zoneservice.client.FeignClient;
import com.agms.zoneservice.dto.DeviceRequest;
import org.springframework.web.bind.annotation.PostMapping;

public @interface EnableFeignClients {
    @FeignClient(name = "sensor-service", url = "")
    public interface SensorClient {

        @PostMapping("/api/devices")
        String registerDevice(DeviceRequest request);

    }
}
