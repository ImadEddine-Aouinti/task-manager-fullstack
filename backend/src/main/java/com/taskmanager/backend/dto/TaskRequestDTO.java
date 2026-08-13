package com.taskmanager.backend.dto;

import com.taskmanager.backend.entity.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskRequestDTO(

        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
        String title,

        @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
        String description,

        TaskStatus status,

        @FutureOrPresent(message = "La date d'échéance doit être aujourd'hui ou dans le futur")
        LocalDateTime dueDate
) {
}
