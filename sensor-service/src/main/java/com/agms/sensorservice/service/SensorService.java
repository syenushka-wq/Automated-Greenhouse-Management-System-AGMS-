package com.agms.sensorservice.service;

import org.springframework.stereotype.Service;

@Service
public class SensorService {

    public String getSensorStatus() {
        return "All sensors are OK";
    }
}