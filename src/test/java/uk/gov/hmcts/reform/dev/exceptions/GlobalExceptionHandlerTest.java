package uk.gov.hmcts.reform.dev.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleTaskNotFound_shouldReturn404() {
        TaskNotFoundException ex = new TaskNotFoundException(1L);
        ResponseEntity<Map<String, Object>> response = handler.handleTaskNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Task not found with id: 1", response.getBody().get("message"));
    }

    @Test
    void handleIllegalArgument_shouldReturn400() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad value");
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Bad value", response.getBody().get("message"));
    }
}
