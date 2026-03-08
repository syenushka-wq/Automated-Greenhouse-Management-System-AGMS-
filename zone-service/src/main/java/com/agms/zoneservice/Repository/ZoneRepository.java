package com.agms.zoneservice.Repository;

import com.agms.zoneservice.model.Zone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends MongoRepository<Zone, String> {
    Optional<Zone> findByDeviceId(String deviceId);
    List<Zone> findByUserId(String userId);
}