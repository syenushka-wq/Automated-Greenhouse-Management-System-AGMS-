package com.agms.zoneservice.Repository;

import com.agms.zoneservice.Entity.Zone;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest
class ZoneRepositoryTest {

    @Autowired
    private ZoneRepository zoneRepository; // ✅ ZoneRepository inject කරන්න, not itself

    @Test
    void testSaveAndFind() {
        Zone zone = new Zone();
        zone.setName("Test Zone");
        zone.setType(Boolean.parseBoolean("Test"));
        zone.setMinTemp(20.0);
        zone.setMaxTemp(30.0);

        Zone saved = zoneRepository.save(zone);
        assertNotNull(saved.getId());
        System.out.println("Saved Zone ID: " + saved.getId());

        Optional<Zone> found = zoneRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        System.out.println("Found: " + found.isPresent());

        // Cleanup
        zoneRepository.deleteById(saved.getId());
    }
}