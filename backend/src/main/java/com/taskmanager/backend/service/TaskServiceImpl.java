package com.taskmanager.backend.service;

import com.taskmanager.backend.dto.TaskMapper;
import com.taskmanager.backend.dto.TaskRequestDTO;
import com.taskmanager.backend.dto.TaskResponseDTO;
import com.taskmanager.backend.entity.Task;
import com.taskmanager.backend.entity.TaskStatus;
import com.taskmanager.backend.exception.ResourceNotFoundException;
import com.taskmanager.backend.repository.TaskRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO request) {
        Task task = TaskMapper.toEntity(request);
        Task saved = taskRepository.save(task);
        log.info("Tâche créée avec l'id {}", saved.getId());
        return TaskMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        Task task = findTaskOrThrow(id);
        return TaskMapper.toResponseDTO(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks() {

        System.out.println("=== AVANT FIND ALL ===");

        List<Task> tasks = taskRepository.findAll();

        System.out.println("=== APRES FIND ALL ===");
        System.out.println("Nombre de tâches : " + tasks.size());

        List<TaskResponseDTO> result = tasks.stream()
                .map(task -> {
                    System.out.println("Mapping task ID : " + task.getId());
                    return TaskMapper.toResponseDTO(task);
                })
                .toList();

        System.out.println("=== APRES MAPPING ===");

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status)
                .stream()
                .map(TaskMapper::toResponseDTO)
                .toList();
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        Task task = findTaskOrThrow(id);
        TaskMapper.updateEntityFromDTO(task, request);
        Task updated = taskRepository.save(task);
        log.info("Tâche {} mise à jour", id);
        return TaskMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = findTaskOrThrow(id);
        taskRepository.delete(task);
        log.info("Tâche {} supprimée", id);
    }

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forTask(id));
    }
}
