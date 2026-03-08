package com.agms.zoneservice.Repository;

import com.agms.zoneservice.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
}