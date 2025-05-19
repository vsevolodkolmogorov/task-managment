package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.effectivemobile.taskmanagement.model.enums.PriorityCode;
import ru.effectivemobile.taskmanagement.model.enums.StatusCode;
import ru.effectivemobile.taskmanagement.service.TaskService;
import ru.effectivemobile.taskmanagement.util.CurrentUserProvider;
import ru.effectivemobile.taskmanagement.validation.OnCreate;
import ru.effectivemobile.taskmanagement.validation.OnUpdate;

/**
 * REST controller for managing tasks.
 * Provides endpoints for users and admins to create, retrieve, update, and delete tasks.
 */
@Slf4j
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
        log.info("Creating task for user with id={}. Task data: {}", currentUserProvider.getCurrentUser().getId(), userDto);
        TaskResponseDto created = taskService.createUserTask(userDto);
        log.info("Task created successfully with id={}", created.getId());
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
        log.info("Admin creating task with data: {}", dto);
        TaskResponseDto created = taskService.createAdminTask(dto);
        log.info("Admin created task successfully with id={}", created.getId());
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
        log.info("Fetching task with id={}", id);
        TaskResponseDto response = taskService.getTask(id);
        log.info("Task with id={} fetched successfully", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of tasks filtered by optional parameters.
     * Users only see their tasks, admins can filter by author and assignee.
     *
     * @param statusCode     task status filter
     * @param priorityCode   task priority filter
     * @param authorId   author ID filter (admin only)
     * @param assigneeId assignee ID filter (admin only)
     * @param page       page number
     * @param size       page size
     * @return paginated list of tasks
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<TaskResponseDto>> getAllTasks(
            @Parameter(description = "Filter by task status") @RequestParam(required = false) StatusCode statusCode,
            @Parameter(description = "Filter by task priority") @RequestParam(required = false) PriorityCode priorityCode,
            @Parameter(description = "Filter by author ID (Admins only)") @RequestParam(required = false) Long authorId,
            @Parameter(description = "Filter by assignee ID (Admins only)") @RequestParam(required = false) Long assigneeId,
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching tasks with filters: status={}, priority={}, authorId={}, assigneeId={}, page={}, size={}",
                statusCode, priorityCode, authorId, assigneeId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskResponseDto> tasks = taskService.getAllTasks(statusCode, priorityCode, authorId, assigneeId, pageable);
        log.info("Fetched {} tasks for page {} of size {}", tasks.getTotalElements(), page, size);
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Validated(OnUpdate.class)
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestUserDto userDto) {
        log.info("Updating task with id={}. New data: {}", id, userDto);
        TaskResponseDto updated = taskService.updateUserTask(id, userDto);
        log.info("Task with id={} updated successfully", id);
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
        log.info("Admin updating task with id={}. New data: {}", id, adminDto);
        TaskResponseDto updated = taskService.updateAdminTask(id, adminDto);
        log.info("Admin updated task with id={} successfully", id);
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
        log.info("Deleting task with id={}", id);
        taskService.deleteTask(id);
        log.info("Task with id={} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
