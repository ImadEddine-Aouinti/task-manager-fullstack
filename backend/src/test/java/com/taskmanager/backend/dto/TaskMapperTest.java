package com.taskmanager.backend.dto;


import com.taskmanager.backend.entity.Task;
import com.taskmanager.backend.entity.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TaskMapper")
class TaskMapperTest {

    @Test
    @DisplayName("toResponseDTO copie fidèlement tous les champs de l'entité")
    void shouldMapEntityToResponseDTO() {
        LocalDateTime now = LocalDateTime.now();
        Task task = Task.builder()
                .id(5L)
                .title("Titre")
                .description("Description")
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(now.plusDays(3))
                .createdAt(now)
                .updatedAt(now)
                .build();

        TaskResponseDTO dto = TaskMapper.toResponseDTO(task);

        assertThat(dto.id()).isEqualTo(5L);
        assertThat(dto.title()).isEqualTo("Titre");
        assertThat(dto.description()).isEqualTo("Description");
        assertThat(dto.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(dto.dueDate()).isEqualTo(task.getDueDate());
        assertThat(dto.createdAt()).isEqualTo(now);
        assertThat(dto.updatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toEntity ignore id/createdAt/updatedAt (gérés par la couche persistance)")
    void shouldMapRequestDTOToNewEntity() {
        TaskRequestDTO request = new TaskRequestDTO("Titre", "Desc", TaskStatus.DONE, null);

        Task task = TaskMapper.toEntity(request);

        assertThat(task.getId()).isNull();
        assertThat(task.getTitle()).isEqualTo("Titre");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(task.getCreatedAt()).isNull(); // rempli seulement au @PrePersist
    }

    @Test
    @DisplayName("updateEntityFromDTO met à jour les champs mais conserve le statut existant si absent du DTO")
    void shouldKeepExistingStatusWhenNotProvided() {
        Task existing = Task.builder().id(1L).title("Ancien titre").status(TaskStatus.IN_PROGRESS).build();
        TaskRequestDTO request = new TaskRequestDTO("Nouveau titre", "Nouvelle desc", null, null);

        TaskMapper.updateEntityFromDTO(existing, request);

        assertThat(existing.getTitle()).isEqualTo("Nouveau titre");
        assertThat(existing.getDescription()).isEqualTo("Nouvelle desc");
        assertThat(existing.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS); // inchangé
    }

    @Test
    @DisplayName("updateEntityFromDTO applique le nouveau statut quand il est fourni")
    void shouldOverrideStatusWhenProvided() {
        Task existing = Task.builder().id(1L).title("Titre").status(TaskStatus.TODO).build();
        TaskRequestDTO request = new TaskRequestDTO("Titre", null, TaskStatus.DONE, null);

        TaskMapper.updateEntityFromDTO(existing, request);

        assertThat(existing.getStatus()).isEqualTo(TaskStatus.DONE);
    }
}
