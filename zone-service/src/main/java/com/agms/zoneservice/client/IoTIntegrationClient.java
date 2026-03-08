package com.agms.zoneservice.client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "iot-integration", url = "${iot.external.api.url}")
public interface IoTIntegrationClient {

    @PostMapping("/devices")
    Map<String, Object> registerDevice(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> deviceRequest
    );
}