package com.agms.zoneservice.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "device-service", url = "${device.service.url}")
public interface DeviceServiceClient {

    @GetMapping("/api/devices/{id}")
    Object getDevice(@PathVariable("id") String id);

}