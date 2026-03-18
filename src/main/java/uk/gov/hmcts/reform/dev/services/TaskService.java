package uk.gov.hmcts.reform.dev.services;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;

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

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
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