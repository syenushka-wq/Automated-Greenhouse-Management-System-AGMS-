package com.agms.zoneservice.Repository;

import com.agms.zoneservice.Entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
}