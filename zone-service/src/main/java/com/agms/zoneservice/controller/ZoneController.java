package com.agms.zoneservice.controller;

import com.agms.zoneservice.Entity.Zone;
import com.agms.zoneservice.dto.ZoneRequest;
import com.agms.zoneservice.service.ZoneService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping
    public Zone createZone(@RequestBody ZoneRequest request) {
        return zoneService.createZone(request);
    }
}