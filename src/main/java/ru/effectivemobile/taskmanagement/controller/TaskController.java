package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import ru.effectivemobile.taskmanagement.service.TaskService;
import ru.effectivemobile.taskmanagement.util.CurrentUserProvider;
import ru.effectivemobile.taskmanagement.validation.OnCreate;
import ru.effectivemobile.taskmanagement.validation.OnUpdate;

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
    @Validated(OnCreate.class)
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestUserDto userDto) {
        TaskResponseDto created = taskService.createUserTask(userDto);
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
    @Validated(OnCreate.class)
    public ResponseEntity<TaskResponseDto> createTaskForAdmin(@Valid @RequestBody TaskRequestAdminDto dto) {
        TaskResponseDto created = taskService.createAdminTask(dto);
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
    public ResponseEntity<Page<TaskResponseDto>> getAllTasks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.getAllTasks(pageable));
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
    @Validated(OnUpdate.class)
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestUserDto userDto) {
        return ResponseEntity.ok(taskService.updateUserTask(id, userDto));
    }

    /**
     * Updates a task with full access. Only available to ADMIN users.
     *
     * @param id the task ID
     * @param adminDto the full update payload
     * @return the updated task
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Validated(OnUpdate.class)
    public ResponseEntity<TaskResponseDto> updateTaskAdmin(@PathVariable Long id, @Valid @RequestBody TaskRequestAdminDto adminDto) {
        return ResponseEntity.ok(taskService.updateAdminTask(id, adminDto));
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
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}



