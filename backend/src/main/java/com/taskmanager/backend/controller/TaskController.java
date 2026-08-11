package com.taskmanager.backend.controller;


import com.taskmanager.backend.dto.TaskRequestDTO;
import com.taskmanager.backend.dto.TaskResponseDTO;
import com.taskmanager.backend.entity.TaskStatus;
import com.taskmanager.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO request) {
        TaskResponseDTO created = taskService.createTask(request);
        URI location = URI.create("/api/v1/tasks/" + created.id());
        return ResponseEntity.created(location).body(created);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }


    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(
            @RequestParam(required = false) TaskStatus status) {

        List<TaskResponseDTO> tasks = (status != null)
                ? taskService.getTasksByStatus(status)
                : taskService.getAllTasks();

        return ResponseEntity.ok(tasks);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request) {

        return ResponseEntity.ok(taskService.updateTask(id, request));
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
