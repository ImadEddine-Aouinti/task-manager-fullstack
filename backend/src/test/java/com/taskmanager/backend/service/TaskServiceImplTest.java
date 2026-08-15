package com.taskmanager.backend.service;

import com.taskmanager.backend.dto.TaskRequestDTO;
import com.taskmanager.backend.dto.TaskResponseDTO;
import com.taskmanager.backend.entity.Task;
import com.taskmanager.backend.entity.TaskStatus;
import com.taskmanager.backend.exception.ResourceNotFoundException;
import com.taskmanager.backend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskServiceImpl")
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task existingTask;

    private static Task.TaskBuilder aTaskBuilder() {
        return Task.builder();
    }

    @BeforeEach
    void setUp() {
        existingTask = aTaskBuilder().id(42L).build();
    }

    @Nested
    @DisplayName("createTask")
    class CreateTask {

        @Test
        @DisplayName("sauvegarde la tâche construite à partir de la requête et retourne le DTO correspondant")
        void shouldCreateAndReturnTask() {
            TaskRequestDTO request = new TaskRequestDTO("Nouvelle tâche", "Description", TaskStatus.TODO, null);

            given(taskRepository.save(any(Task.class))).willAnswer(invocation -> {
                Task toSave = invocation.getArgument(0);
                toSave.setId(1L);
                return toSave;
            });

            TaskResponseDTO result = taskService.createTask(request);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("Nouvelle tâche");
            assertThat(result.status()).isEqualTo(TaskStatus.TODO);

            // Vérifie que le repository a bien été sollicité une seule fois, avec les bonnes données.
            ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository, times(1)).save(captor.capture());
            assertThat(captor.getValue().getTitle()).isEqualTo("Nouvelle tâche");
        }
    }

    @Nested
    @DisplayName("getTaskById")
    class GetTaskById {

        @Test
        @DisplayName("retourne la tâche quand elle existe")
        void shouldReturnTaskWhenFound() {
            given(taskRepository.findById(42L)).willReturn(Optional.of(existingTask));

            TaskResponseDTO result = taskService.getTaskById(42L);

            assertThat(result.id()).isEqualTo(42L);
            assertThat(result.title()).isEqualTo(existingTask.getTitle());
        }

        @Test
        @DisplayName("lève ResourceNotFoundException quand la tâche n'existe pas")
        void shouldThrowWhenNotFound() {
            given(taskRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.getTaskById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("999");

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getAllTasks / getTasksByStatus")
    class ListTasks {

        @Test
        @DisplayName("retourne la liste complète mappée en DTO")
        void shouldReturnAllTasks() {
            given(taskRepository.findAll()).willReturn(List.of(existingTask));

            List<TaskResponseDTO> result = taskService.getAllTasks();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(existingTask.getId());
        }

        @Test
        @DisplayName("retourne une liste vide si aucune tâche n'existe")
        void shouldReturnEmptyListWhenNoTasks() {
            given(taskRepository.findAll()).willReturn(List.of());

            assertThat(taskService.getAllTasks()).isEmpty();
        }

        @Test
        @DisplayName("délègue le filtrage par statut au repository")
        void shouldFilterByStatus() {
            Task doneTask = aTaskBuilder().id(2L).status(TaskStatus.DONE).build();
            given(taskRepository.findByStatus(TaskStatus.DONE)).willReturn(List.of(doneTask));

            List<TaskResponseDTO> result = taskService.getTasksByStatus(TaskStatus.DONE);

            assertThat(result).extracting(TaskResponseDTO::status).containsOnly(TaskStatus.DONE);
            verify(taskRepository).findByStatus(TaskStatus.DONE);
        }
    }

    @Nested
    @DisplayName("updateTask")
    class UpdateTask {

        @Test
        @DisplayName("met à jour les champs et sauvegarde l'entité existante")
        void shouldUpdateExistingTask() {
            TaskRequestDTO request = new TaskRequestDTO("Titre modifié", "Nouvelle description", TaskStatus.DONE, null);

            given(taskRepository.findById(42L)).willReturn(Optional.of(existingTask));
            given(taskRepository.save(any(Task.class))).willAnswer(inv -> inv.getArgument(0));

            TaskResponseDTO result = taskService.updateTask(42L, request);

            assertThat(result.title()).isEqualTo("Titre modifié");
            assertThat(result.status()).isEqualTo(TaskStatus.DONE);
            verify(taskRepository).save(existingTask);
        }

        @Test
        @DisplayName("lève ResourceNotFoundException si la tâche à modifier n'existe pas")
        void shouldThrowWhenTaskToUpdateNotFound() {
            given(taskRepository.findById(999L)).willReturn(Optional.empty());
            TaskRequestDTO request = new TaskRequestDTO("Titre", null, null, null);

            assertThatThrownBy(() -> taskService.updateTask(999L, request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteTask")
    class DeleteTask {

        @Test
        @DisplayName("supprime la tâche quand elle existe")
        void shouldDeleteExistingTask() {
            given(taskRepository.findById(42L)).willReturn(Optional.of(existingTask));

            taskService.deleteTask(42L);

            verify(taskRepository).delete(existingTask);
        }

        @Test
        @DisplayName("lève ResourceNotFoundException et ne supprime rien si la tâche n'existe pas")
        void shouldThrowWhenTaskToDeleteNotFound() {
            given(taskRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.deleteTask(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(taskRepository, never()).delete(any(Task.class));
        }
    }
}
