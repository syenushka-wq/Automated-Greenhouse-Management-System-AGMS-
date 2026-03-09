package com.agms.automationservice.service;

import com.agms.automationservice.Model.Task;
import com.agms.automationservice.repository.AutomationRepository;
import com.agms.automationservice.service.AutomationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutomationServiceImpl implements AutomationService {

    private final AutomationRepository repository;

    public AutomationServiceImpl(AutomationRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    @Override
    public Task createTask(Task task) {
        return null;
    }

}