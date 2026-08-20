package com.taskmanager.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.backend.dto.TaskRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Task API - tests d'intégration de bout en bout")
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /api/v1/tasks crée la tâche et la persiste réellement en base")
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("Rédiger le rapport", "Rapport mensuel", null, null);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Rédiger le rapport"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("POST /api/v1/tasks avec un titre vide renvoie 400 et le détail de l'erreur")
    void shouldReturnValidationErrorWhenTitleIsBlank() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("", null, null, null);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists());
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} sur une tâche inexistante renvoie 404")
    void shouldReturnNotFoundForUnknownTask() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/v1/tasks/9999"));
    }

    @Test
    @DisplayName("Le cycle complet créer -> lire -> mettre à jour -> supprimer fonctionne de bout en bout")
    void shouldSupportFullLifecycle() throws Exception {
        TaskRequestDTO createRequest = new TaskRequestDTO("Préparer le sprint", "Backlog grooming", null, null);

        String response = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // Lecture
        mockMvc.perform(get("/api/v1/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Préparer le sprint"));

        // Mise à jour
        TaskRequestDTO updateRequest = new TaskRequestDTO("Préparer le sprint", "Backlog groomé",
                com.taskmanager.backend.entity.TaskStatus.IN_PROGRESS, null);

        mockMvc.perform(put("/api/v1/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // Filtrage par statut
        mockMvc.perform(get("/api/v1/tasks").param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());

        // Suppression
        mockMvc.perform(delete("/api/v1/tasks/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/tasks/{id}", id))
                .andExpect(status().isNotFound());
    }
}
