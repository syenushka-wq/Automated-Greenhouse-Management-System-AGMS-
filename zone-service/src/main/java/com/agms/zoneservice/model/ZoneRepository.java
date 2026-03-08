package com.agms.zoneservice.model;

import com.agms.zoneservice.Entity.Zone;
import com.agms.zoneservice.Repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends MongoRepository<Zone, String> {
    Optional<Zone> findByDeviceId(String deviceId);
    List<Zone> findByUserId(String userId);
}