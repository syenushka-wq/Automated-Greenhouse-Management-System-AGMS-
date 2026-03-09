package com.agms.sensorservice.service;

import com.agms.sensorservice.model.SensorDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SensorService {

    public List<SensorDTO> getAllSensors() {
        // Example data, replace with DB calls later
        List<SensorDTO> sensors = new ArrayList<>();
        SensorDTO sensor1 = new SensorDTO();
        sensor1.setId("S001");
        sensor1.setName("Temperature Sensor");
        sensor1.setValue(25.5);

        SensorDTO sensor2 = new SensorDTO();
        sensor2.setId("S002");
        sensor2.setName("Humidity Sensor");
        sensor2.setValue(60.0);

        sensors.add(sensor1);
        sensors.add(sensor2);

        return sensors;
    }
}