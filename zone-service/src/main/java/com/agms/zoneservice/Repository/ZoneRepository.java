package com.agms.zoneservice.Repository;

import com.agms.zoneservice.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, String> {

    Optional<Zone> findByDeviceId(String deviceId);

    List<Zone> findByUserId(String userId);

}