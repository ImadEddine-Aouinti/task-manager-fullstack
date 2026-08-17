package com.taskmanager.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.backend.dto.TaskRequestDTO;
import com.taskmanager.backend.dto.TaskResponseDTO;
import com.taskmanager.backend.entity.TaskStatus;
import com.taskmanager.backend.exception.ResourceNotFoundException;
import com.taskmanager.backend.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TaskService taskService;

    private TaskResponseDTO sampleResponse() {
        LocalDateTime now = LocalDateTime.now();
        return new TaskResponseDTO(1L, "Préparer la démo", "Description", TaskStatus.TODO, now.plusDays(1), now, now);
    }

    @Test
    @DisplayName("POST /api/v1/tasks avec un corps valide renvoie 201 et l'en-tête Location")
    void shouldCreateTask() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("Préparer la démo", "Description", TaskStatus.TODO, null);
        given(taskService.createTask(any(TaskRequestDTO.class))).willReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/tasks/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Préparer la démo"));
    }

    @Test
    @DisplayName("POST /api/v1/tasks sans titre renvoie 400 avec le détail par champ, sans appeler le service")
    void shouldRejectInvalidPayload() throws Exception {
        TaskRequestDTO invalidRequest = new TaskRequestDTO("  ", null, null, null);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists());

        verify(taskService, org.mockito.Mockito.never()).createTask(any());
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} renvoie 200 avec la tâche demandée")
    void shouldReturnTaskById() throws Exception {
        given(taskService.getTaskById(1L)).willReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} sur une tâche inconnue renvoie 404 avec un message explicite")
    void shouldReturn404WhenTaskMissing() throws Exception {
        given(taskService.getTaskById(99L)).willThrow(ResourceNotFoundException.forTask(99L));

        mockMvc.perform(get("/api/v1/tasks/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Aucune tâche trouvée avec l'id : 99"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks sans paramètre renvoie toutes les tâches")
    void shouldReturnAllTasksWhenNoFilter() throws Exception {
        given(taskService.getAllTasks()).willReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(taskService).getAllTasks();
    }

    @Test
    @DisplayName("GET /api/v1/tasks?status=DONE délègue le filtrage au service")
    void shouldFilterTasksByStatus() throws Exception {
        given(taskService.getTasksByStatus(TaskStatus.DONE)).willReturn(List.of());

        mockMvc.perform(get("/api/v1/tasks").param("status", "DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(taskService).getTasksByStatus(TaskStatus.DONE);
        verify(taskService, org.mockito.Mockito.never()).getAllTasks();
    }

    @Test
    @DisplayName("GET /api/v1/tasks?status=INVALIDE renvoie 400 (mauvaise valeur d'enum)")
    void shouldReturn400ForInvalidStatusParam() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").param("status", "INVALIDE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} met à jour et renvoie 200")
    void shouldUpdateTask() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("Titre modifié", null, TaskStatus.DONE, null);
        given(taskService.updateTask(eq(1L), any(TaskRequestDTO.class))).willReturn(sampleResponse());

        mockMvc.perform(put("/api/v1/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} renvoie 204 et appelle le service une seule fois")
    void shouldDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(taskService, org.mockito.Mockito.times(1)).deleteTask(1L);
    }
}
