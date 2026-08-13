package com.taskmanager.backend.service;

import com.taskmanager.backend.dto.TaskRequestDTO;
import com.taskmanager.backend.dto.TaskResponseDTO;
import com.taskmanager.backend.entity.TaskStatus;

import java.util.List;

public interface TaskService {

    TaskResponseDTO createTask(TaskRequestDTO request);

    TaskResponseDTO getTaskById(Long id);

    List<TaskResponseDTO> getAllTasks();

    List<TaskResponseDTO> getTasksByStatus(TaskStatus status);

    TaskResponseDTO updateTask(Long id, TaskRequestDTO request);

    void deleteTask(Long id);
}
