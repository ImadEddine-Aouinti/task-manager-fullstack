package com.taskmanager.backend.e2e;

import com.taskmanager.backend.e2e.pages.TaskBoardPage;
import com.taskmanager.backend.e2e.pages.TaskFormModal;
import com.taskmanager.backend.e2e.support.BaseE2ETest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@DisplayName("Parcours utilisateur - gestion des tâches (Selenium)")
class TaskManagementE2ETest extends BaseE2ETest {

    @Test
    @DisplayName("Un utilisateur peut créer une tâche et la voir apparaître dans la liste")
    void shouldCreateTaskAndSeeItInList() {
        TaskBoardPage board = new TaskBoardPage(driver, wait).open(BASE_URL);

        board.clickNewTask()
                .fillTitle("Tâche créée par Selenium")
                .fillDescription("Test end-to-end du parcours de création")
                .submit()
                .waitUntilClosed();

        assertThat(board.hasTaskWithTitle("Tâche créée par Selenium")).isTrue();
    }

    @Test
    @DisplayName("La création échoue et affiche une erreur de validation si le titre est vide")
    void shouldShowValidationErrorWhenTitleEmpty() {
        TaskBoardPage board = new TaskBoardPage(driver, wait).open(BASE_URL);

        TaskFormModal modal = board.clickNewTask()
                .fillTitle("   ")
                .submit();

        assertThat(modal.hasTitleError()).isTrue();
    }

    @Test
    @DisplayName("Un utilisateur peut modifier le statut d'une tâche existante")
    void shouldUpdateTaskStatus() {
        TaskBoardPage board = new TaskBoardPage(driver, wait).open(BASE_URL);
        board.clickNewTask().fillTitle("Tâche à faire évoluer").submit().waitUntilClosed();

        board.editTask("Tâche à faire évoluer")
                .selectStatus("Terminée")
                .submit()
                .waitUntilClosed();

        board.filterByStatus("DONE");
        assertThat(board.hasTaskWithTitle("Tâche à faire évoluer")).isTrue();
    }

    @Test
    @DisplayName("Un utilisateur peut supprimer une tâche existante")
    void shouldDeleteTask() {
        TaskBoardPage board = new TaskBoardPage(driver, wait).open(BASE_URL);
        board.clickNewTask().fillTitle("Tâche à supprimer").submit().waitUntilClosed();
        assertThat(board.hasTaskWithTitle("Tâche à supprimer")).isTrue();

        board.deleteTask("Tâche à supprimer");

        assertThat(board.hasTaskWithTitle("Tâche à supprimer")).isFalse();
    }
}
