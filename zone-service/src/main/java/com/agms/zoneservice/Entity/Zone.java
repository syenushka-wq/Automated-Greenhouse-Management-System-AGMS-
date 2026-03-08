package com.agms.zoneservice.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double minTemp;

    private double maxTemp;

    private String deviceId;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setUpdatedAt(LocalDateTime now) {

    }

    public void setId(String string) {
    }

    public void setCreatedAt(LocalDateTime now) {
    }

    public void setType(boolean type) {
    }
}