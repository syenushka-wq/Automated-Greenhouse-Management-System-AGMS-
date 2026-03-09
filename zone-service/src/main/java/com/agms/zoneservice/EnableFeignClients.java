package com.agms.zoneservice;

import com.agms.zoneservice.dto.DeviceRequest;
import org.springframework.web.bind.annotation.PostMapping;

public @interface EnableFeignClients {
    public interface SensorClient {

        @PostMapping("/api/devices")
        String registerDevice(DeviceRequest request);

    }
}
