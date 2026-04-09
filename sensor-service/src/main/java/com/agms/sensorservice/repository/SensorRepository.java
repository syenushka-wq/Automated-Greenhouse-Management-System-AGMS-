package com.agms.sensorservice.repository;

import com.agms.sensorservice.model.SensorDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<SensorDTO, String> {
}