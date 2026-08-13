package com.taskmanager.backend.repository;

import com.taskmanager.backend.entity.Task;
import com.taskmanager.backend.entity.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("TaskRepository")
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("renseigne automatiquement createdAt/updatedAt et le statut par défaut à la persistance")
    void shouldPopulateAuditFieldsAndDefaultStatusOnPersist() {
        Task task = Task.builder().title("Nouvelle tâche").build();

        Task saved = taskRepository.saveAndFlush(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("findByStatus ne retourne que les tâches correspondant au statut demandé")
    void shouldFindTasksByStatus() {
        entityManager.persist(Task.builder().title("Tâche A").status(TaskStatus.TODO).build());
        entityManager.persist(Task.builder().title("Tâche B").status(TaskStatus.DONE).build());
        entityManager.persist(Task.builder().title("Tâche C").status(TaskStatus.DONE).build());
        entityManager.flush();

        List<Task> doneTasks = taskRepository.findByStatus(TaskStatus.DONE);

        assertThat(doneTasks).hasSize(2)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Tâche B", "Tâche C");
    }

    @Test
    @DisplayName("findByStatus renvoie une liste vide si aucune tâche ne correspond")
    void shouldReturnEmptyListWhenNoMatch() {
        entityManager.persist(Task.builder().title("Tâche A").status(TaskStatus.TODO).build());
        entityManager.flush();

        assertThat(taskRepository.findByStatus(TaskStatus.CANCELLED)).isEmpty();
    }

    @Test
    @DisplayName("met à jour updatedAt lors d'une modification")
    void shouldUpdateTimestampOnUpdate() {
        Task saved = entityManager.persistFlushFind(Task.builder().title("Tâche à modifier").build());
        var initialUpdatedAt = saved.getUpdatedAt();

        saved.setTitle("Titre modifié");
        Task updated = taskRepository.saveAndFlush(saved);
        entityManager.refresh(updated);

        assertThat(updated.getTitle()).isEqualTo("Titre modifié");
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }
}
