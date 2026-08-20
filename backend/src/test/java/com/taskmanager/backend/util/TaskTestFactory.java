package com.taskmanager.backend.util;

import com.taskmanager.backend.dto.TaskRequestDTO;
import com.taskmanager.backend.entity.Task;
import com.taskmanager.backend.entity.TaskStatus;

import java.time.LocalDateTime;

public final class TaskTestFactory {

    private TaskTestFactory() {
    }

    public static Task aTask() {
        return aTaskBuilder().build();
    }

    public static Task.TaskBuilder aTaskBuilder() {
        LocalDateTime now = LocalDateTime.now();
        return Task.builder()
                .id(1L)
                .title("Préparer la démo client")
                .description("Vérifier le scénario de bout en bout")
                .status(TaskStatus.TODO)
                .dueDate(now.plusDays(2))
                .createdAt(now)
                .updatedAt(now);
    }

    public static TaskRequestDTO aValidRequest() {
        return new TaskRequestDTO(
                "Préparer la démo client",
                "Vérifier le scénario de bout en bout",
                TaskStatus.TODO,
                LocalDateTime.now().plusDays(2)
        );
    }

    public static TaskRequestDTO aRequestWithBlankTitle() {
        return new TaskRequestDTO("", "Une description valide", null, null);
    }
}
