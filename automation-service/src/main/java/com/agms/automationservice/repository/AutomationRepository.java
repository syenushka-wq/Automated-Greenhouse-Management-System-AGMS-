package com.agms.automationservice.repository;

import com.agms.automationservice.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutomationRepository extends JpaRepository<Task, Long> {
    // Add custom queries if needed
}