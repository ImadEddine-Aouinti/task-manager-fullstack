package com.taskmanager.backend.dto;

import com.taskmanager.backend.entity.Task;

public final class TaskMapper {

    private TaskMapper() {
    }

    public static TaskResponseDTO toResponseDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public static Task toEntity(TaskRequestDTO dto) {
        return Task.builder()
                .title(dto.title())
                .description(dto.description())
                .status(dto.status())
                .dueDate(dto.dueDate())
                .build();
    }


    public static void updateEntityFromDTO(Task task, TaskRequestDTO dto) {
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        if (dto.status() != null) {
            task.setStatus(dto.status());
        }
        task.setDueDate(dto.dueDate());
    }
}

