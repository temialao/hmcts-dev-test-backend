package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test task");
        task.setDescription("Test description");
        task.setStatus(TaskStatus.PENDING);
        task.setDueDateTime(LocalDateTime.of(2027, 3, 20, 10, 0));
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createTask_shouldReturn201() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        String json = "{\"title\":\"Test task\",\"description\":\"Test description\",\"status\":\"PENDING\",\"dueDateTime\":\"2027-03-20T10:00:00\"}";

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test task"))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createTask_shouldReturn400_whenTitleMissing() throws Exception {
        String json = "{\"description\":\"No title\",\"status\":\"PENDING\",\"dueDateTime\":\"2027-03-20T10:00:00\"}";

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_shouldReturn400_whenStatusMissing() throws Exception {
        String json = "{\"title\":\"Test\",\"dueDateTime\":\"2027-03-20T10:00:00\"}";

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskById_shouldReturn200() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test task"));
    }

    @Test
    void getTaskById_shouldReturn404_whenNotFound() throws Exception {
        when(taskService.getTaskById(999L)).thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(get("/tasks/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Task not found with id: 999"));
    }

    @Test
    void getAllTasks_shouldReturn200() throws Exception {
        Page<Task> page = new PageImpl<>(List.of(task));
        when(taskService.searchTasks(eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value("Test task"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllTasks_withStatusFilter_shouldReturn200() throws Exception {
        Page<Task> page = new PageImpl<>(List.of(task));
        when(taskService.searchTasks(eq(null), eq(TaskStatus.PENDING), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tasks?status=PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void getAllTasks_withSearchFilter_shouldReturn200() throws Exception {
        Page<Task> page = new PageImpl<>(List.of(task));
        when(taskService.searchTasks(eq("Test"), eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tasks?search=Test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value("Test task"));
    }

    @Test
    void updateTaskStatus_shouldReturn200() throws Exception {
        task.setStatus(TaskStatus.COMPLETED);
        when(taskService.updateTaskStatus(eq(1L), eq(TaskStatus.COMPLETED))).thenReturn(task);

        mockMvc.perform(patch("/tasks/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"COMPLETED\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void updateTaskStatus_shouldReturn404_whenNotFound() throws Exception {
        when(taskService.updateTaskStatus(eq(999L), any(TaskStatus.class)))
            .thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(patch("/tasks/999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"COMPLETED\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_shouldReturn204() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new TaskNotFoundException(999L)).when(taskService).deleteTask(999L);

        mockMvc.perform(delete("/tasks/999"))
            .andExpect(status().isNotFound());
    }
}
