package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.taskmanagement.dto.TaskRequestAdminDto;
import ru.effectivemobile.taskmanagement.dto.TaskRequestUserDto;
import ru.effectivemobile.taskmanagement.dto.TaskResponseDto;
import ru.effectivemobile.taskmanagement.model.Priority;
import ru.effectivemobile.taskmanagement.model.Status;
import ru.effectivemobile.taskmanagement.service.TaskService;
import ru.effectivemobile.taskmanagement.util.CurrentUserProvider;
import ru.effectivemobile.taskmanagement.validation.OnCreate;
import ru.effectivemobile.taskmanagement.validation.OnUpdate;

/**
 * REST controller for managing tasks.
 * Provides endpoints for users and admins to create, retrieve, update, and delete tasks.
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Endpoints for working with tasks")
public class TaskController {

    private final TaskService taskService;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Creates a new task by a regular user.
     *
     * @param userDto the task data
     * @return the created task
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Validated(OnCreate.class)
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestUserDto userDto) {
        TaskResponseDto created = taskService.createUserTask(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Creates a new task by an admin.
     *
     * @param dto the task data including assignee
     * @return the created task
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Validated(OnCreate.class)
    public ResponseEntity<TaskResponseDto> createTaskForAdmin(@Valid @RequestBody TaskRequestAdminDto dto) {
        TaskResponseDto created = taskService.createAdminTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the task ID
     * @return the task
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable Long id) {
        TaskResponseDto response = taskService.getTask(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of tasks filtered by optional parameters.
     * Users only see their tasks, admins can filter by author and assignee.
     *
     * @param status     task status filter
     * @param priority   task priority filter
     * @param authorId   author ID filter (admin only)
     * @param assigneeId assignee ID filter (admin only)
     * @param page       page number
     * @param size       page size
     * @return paginated list of tasks
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<TaskResponseDto>> getAllTasks(
            @Parameter(description = "Filter by task status") @RequestParam(required = false) Status status,
            @Parameter(description = "Filter by task priority") @RequestParam(required = false) Priority priority,
            @Parameter(description = "Filter by author ID (Admins only)") @RequestParam(required = false) Long authorId,
            @Parameter(description = "Filter by assignee ID (Admins only)") @RequestParam(required = false) Long assigneeId,
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskResponseDto> tasks = taskService.getAllTasks(status, priority, authorId, assigneeId, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Updates a task as a regular user. Only authors can update full task; assignees can update status.
     *
     * @param id      task ID
     * @param userDto updated task data
     * @return the updated task
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Validated(OnUpdate.class)
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestUserDto userDto) {
        TaskResponseDto updated = taskService.updateUserTask(id, userDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Updates a task as an admin.
     *
     * @param id       task ID
     * @param adminDto updated task data including assignee
     * @return the updated task
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Validated(OnUpdate.class)
    public ResponseEntity<TaskResponseDto> updateTaskAdmin(@PathVariable Long id, @Valid @RequestBody TaskRequestAdminDto adminDto) {
        TaskResponseDto updated = taskService.updateAdminTask(id, adminDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a task by ID. Users can only delete tasks they authored.
     *
     * @param id the task ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
