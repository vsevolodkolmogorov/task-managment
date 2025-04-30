package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.taskmanagement.dto.TaskRequestDto;
import ru.effectivemobile.taskmanagement.dto.TaskRequestUserDto;
import ru.effectivemobile.taskmanagement.dto.TaskResponseDto;
import ru.effectivemobile.taskmanagement.model.Role;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.service.TaskService;
import ru.effectivemobile.taskmanagement.util.CurrentUserProvider;

import java.util.List;

/**
 * REST controller for managing tasks in the Task Management system.
 *
 * <p>This controller provides endpoints for creating, retrieving, updating, and deleting tasks.
 * It supports two user roles: ADMIN and USER. Authentication is handled via JWT tokens,
 * and access to endpoints is protected using role-based authorization.</p>
 *
 * <p>Key features:
 * <ul>
 *     <li>Authenticated task creation and updates</li>
 *     <li>Role-based permissions (ADMIN vs USER)</li>
 *     <li>Task ownership and assignment handling</li>
 * </ul>
 * </p>
 *
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Creates a new task for the current authenticated user (as both author and assignee).
     *
     * @param userDto the task data submitted by the user
     * @return the created task
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestUserDto userDto) {
        User user = currentUserProvider.getCurrentUser();

        TaskRequestDto dto = TaskRequestDto.builder()
                .title(userDto.getTitle())
                .description(userDto.getDescription())
                .status(userDto.getStatus())
                .priority(userDto.getPriority())
                .authorId(user.getId())
                .assigneeId(user.getId())
                .build();

        TaskResponseDto created = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Creates a task with a custom assignee. Only accessible to ADMIN users.
     *
     * @param dto the full task DTO including assigneeId
     * @return the created task
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDto> createTaskForAdmin(@Valid @RequestBody TaskRequestDto dto) {
        User user = currentUserProvider.getCurrentUser();
        dto.setAuthorId(user.getId());
        TaskResponseDto created = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves a task by ID.
     * Users can access only their own assigned or authored tasks, admins can access any task.
     *
     * @param id the task ID
     * @return the task details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable Long id) {
        User user = currentUserProvider.getCurrentUser();

        if (user.getRole().equals(Role.USER)) {
            return ResponseEntity.ok(taskService.getUserTask(id, user.getId()));
        }

        return ResponseEntity.ok(taskService.getTask(id));
    }

    /**
     * Retrieves all tasks.
     * Admins receive all tasks; users receive only their own.
     *
     * @return a list of tasks
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        User user = currentUserProvider.getCurrentUser();

        if (user.getRole().equals(Role.USER)) {
            return ResponseEntity.ok(taskService.getAllUsersTasks(user.getId()));
        }

        return ResponseEntity.ok(taskService.getAllTasks());
    }

    /**
     * Updates a task for a user. Only the task author or assignee can perform the update.
     *
     * @param id the task ID
     * @param userDto the update request with permitted fields
     * @return the updated task
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestUserDto userDto) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(taskService.updateUserTask(id, userDto, user));
    }

    /**
     * Updates a task with full access. Only available to ADMIN users.
     *
     * @param id the task ID
     * @param taskDto the full update payload
     * @return the updated task
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDto> updateTaskAdmin(@PathVariable Long id, @Valid @RequestBody TaskRequestDto taskDto) {
        User user = currentUserProvider.getCurrentUser();

        TaskRequestDto dto = TaskRequestDto.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus())
                .priority(taskDto.getPriority())
                .authorId(user.getId())
                .assigneeId(taskDto.getAssigneeId())
                .build();

        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }

    /**
     * Deletes a task by ID.
     * Users can delete their own authored/assigned tasks; admins can delete any task.
     *
     * @param id the task ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        User user = currentUserProvider.getCurrentUser();
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }
}



