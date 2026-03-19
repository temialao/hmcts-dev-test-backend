package uk.gov.hmcts.reform.dev.services;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@SuppressWarnings("null")
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(@NonNull Task task) {
        task.setId(null);
        return taskRepository.save(task);
    }

    public Task getTaskById(@NonNull Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<Task> getTasksByStatus(TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }

    public Page<Task> searchTasks(String search, TaskStatus status, Pageable pageable) {
        if (search != null && status != null) {
            return taskRepository.findByTitleContainingIgnoreCaseAndStatus(search, status, pageable);
        }
        if (search != null) {
            return taskRepository.findByTitleContainingIgnoreCase(search, pageable);
        }
        if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    public Task updateTaskStatus(@NonNull Long id, TaskStatus status) {
        Task task = getTaskById(id);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public void deleteTask(@NonNull Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}