package com.agms.zoneservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ZoneDTO {

    private String id;

    @NotBlank(message = "Zone name is required")
    private String name;

    private String deviceId;

    private String userId;

}