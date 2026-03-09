package com.agms.automationservice.service;

import com.agms.automationservice.Model.Task;

import java.util.List;

public interface AutomationService {
    List<Task> getAllTasks();
    Task createTask(Task task);
}