package com.agms.zoneservice.service;

import lombok.Data;

@Data
public class ZoneDTO {
    private String id;
    private String name;
    private String type;
    private Double minTemp;
    private Double maxTemp;
    private String deviceId;
    private String userId;
}