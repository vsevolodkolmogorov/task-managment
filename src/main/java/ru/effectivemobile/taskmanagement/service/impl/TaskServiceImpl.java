package ru.effectivemobile.taskmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.effectivemobile.taskmanagement.dto.*;
import ru.effectivemobile.taskmanagement.exceptions.*;
import ru.effectivemobile.taskmanagement.model.*;
import ru.effectivemobile.taskmanagement.model.enums.PriorityCode;
import ru.effectivemobile.taskmanagement.model.enums.RoleCode;
import ru.effectivemobile.taskmanagement.model.enums.StatusCode;
import ru.effectivemobile.taskmanagement.repository.*;
import ru.effectivemobile.taskmanagement.service.TaskService;
import ru.effectivemobile.taskmanagement.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service implementation for managing tasks.
 * Provides business logic for task CRUD operations, including role-based access control.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Deletes a task by ID.
     * Only the author or an admin can delete a task.
     *
     * @param id the ID of the task to delete
     * @throws TaskNotFoundException if the task does not exist
     * @throws AccessDeniedException if the current user is not allowed to delete the task
     */
    @Override
    public void deleteTask(Long id) {
        User user = currentUserProvider.getCurrentUser();
        logger.info("Attempting to delete task with id {} by user {}", id, user.getUsername());

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));

        if (user.getRole().is(RoleCode.USER) && !task.getAuthor().getId().equals(user.getId())) {
            logger.error("Access denied: user {} is not the author of the task {}", user.getUsername(), id);
            throw new AccessDeniedException("The user is not the author of the task!");
        }

        taskRepository.deleteById(id);
        logger.info("Task with id {} successfully deleted", id);
    }

    /**
     * Updates an existing task with full admin rights.
     * Admins can modify any task fields and assign any user.
     *
     * @param taskId the ID of the task to update
     * @param dto    the data for updating the task
     * @return the updated task as a DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public TaskResponseDto updateAdminTask(Long taskId, TaskRequestAdminDto dto) {
        User user = currentUserProvider.getCurrentUser();
        logger.info("Admin user {} is updating task with id {}", user.getUsername(), taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        applyDtoToTask(dto, task);

        if (dto.getAssigneeId() != null) {
            User assignee = findUserById(dto.getAssigneeId());
            task.setAssignee(assignee);
        }

        task.setAuthor(user);
        task.setId(taskId);
        taskRepository.save(task);

        logger.info("Task with id {} successfully updated by admin {}", taskId, user.getUsername());
        return TaskConverter.toDto(task);
    }

    /**
     * Updates a task with user-level access control.
     * - Author can update title, description, status, priority.
     * - Assignee can only update the status.
     *
     * @param taskId the ID of the task to update
     * @param dto    the data for updating the task
     * @return the updated task as a DTO
     * @throws TaskNotFoundException if the task does not exist
     * @throws AccessDeniedException if the user is neither author nor assignee
     */
    @Override
    public TaskResponseDto updateUserTask(Long taskId, TaskRequestUserDto dto) {
        User user = currentUserProvider.getCurrentUser();
        logger.info("User {} is updating task with id {}", user.getUsername(), taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        boolean isAuthor = task.getAuthor().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee().getId().equals(user.getId());

        if (!isAuthor && !isAssignee) {
            logger.error("Access denied: user {} is neither the author nor assignee of task {}", user.getUsername(), taskId);
            throw new AccessDeniedException("User is neither the author nor the assignee of the task");
        }

        // check for user edite like assignee
        if (isAssignee && !isAuthor && dto.getStatusCode() != null) {
            task.setStatus(statusRepository.findByCode(dto.getStatusCode().name())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status")));
        }

        if (isAuthor) {
            applyDtoToTask(dto, task);
        }

        task.setId(taskId);
        taskRepository.save(task);

        logger.info("Task with id {} successfully updated by user {}", taskId, user.getUsername());
        return TaskConverter.toDto(task);
    }

    /**
     * Creates a new task with admin rights.
     * Admin must specify an assignee.
     *
     * @param dto the task creation data
     * @return the created task as a DTO
     * @throws UserNotFoundException if the assignee does not exist
     */
    @Override
    public TaskResponseDto createAdminTask(TaskRequestAdminDto dto) {
        User user = currentUserProvider.getCurrentUser();
        logger.info("Admin user {} is creating a new task", user.getUsername());

        Status status = statusRepository.findByCode(dto.getStatusCode().name())
                .orElseThrow(() -> new IllegalArgumentException("Invalid status"));

        Priority priority = priorityRepository.findByCode(dto.getPriorityCode().name())
                .orElseThrow(() -> new IllegalArgumentException("Invalid priority"));

        User assignee = findUserById(dto.getAssigneeId());
        Task task = TaskConverter.toEntity(dto, user, assignee, status, priority);

        task.setStatus(statusRepository.findByCode(dto.getStatusCode().name())
                .orElseThrow(() -> new IllegalArgumentException("Invalid status")));

        taskRepository.save(task);

        logger.info("Task with id {} successfully created by admin {}", task.getId(), user.getUsername());
        return TaskConverter.toDto(task);
    }

    /**
     * Creates a new task on behalf of a regular user.
     * The author is the currently authenticated user.
     *
     * @param dto the task creation data
     * @return the created task as a DTO
     */
    @Override
    public TaskResponseDto createUserTask(TaskRequestUserDto dto) {
        User user = currentUserProvider.getCurrentUser();
        logger.info("User {} is creating a new task", user.getUsername());

        Status status = statusRepository.findByCode(dto.getStatusCode().name())
                .orElseThrow(() -> new IllegalArgumentException("Invalid status"));

        Priority priority = priorityRepository.findByCode(dto.getPriorityCode().name())
                .orElseThrow(() -> new IllegalArgumentException("Invalid priority"));

        Task task = TaskConverter.toEntity(dto, user, status, priority);

        taskRepository.save(task);

        logger.info("Task with id {} successfully created by user {}", task.getId(), user.getUsername());
        return TaskConverter.toDto(task);
    }

    /**
     * Retrieves a single task by ID.
     * Regular users can only view their own tasks (as author or assignee).
     *
     * @param id the task ID
     * @return the task as a DTO
     * @throws TaskNotFoundException if the task does not exist or access is denied
     */
    @Override
    public TaskResponseDto getTask(Long id) {
        User user = currentUserProvider.getCurrentUser();
        logger.info("User {} is retrieving task with id {}", user.getUsername(), id);

        Task task;
        if (user.getRole().is(RoleCode.USER)) {
            task = taskRepository.findByIdAuthorAndAssignee(id, user.getId())
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        } else {
            task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        }

        logger.info("Task with id {} retrieved successfully by user {}", id, user.getUsername());
        return TaskConverter.toDto(task);
    }

    /**
     * Retrieves a paginated list of tasks.
     * - Users can see only their own tasks.
     * - Admins can filter by authorId and assigneeId.
     *
     * @param statusCode optional filter by status
     * @param priorityCode   optional filter by priority
     * @param authorId   optional filter (admin only)
     * @param assigneeId optional filter (admin only)
     * @param pageable   pagination info
     * @return a page of task DTOs
     */
    @Override
    public Page<TaskResponseDto> getAllTasks(StatusCode statusCode, PriorityCode priorityCode, Long authorId, Long assigneeId, Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();
        String statusCodeStr = (statusCode != null) ? statusCode.name() : null;
        String priorityCodeStr = (priorityCode != null) ? priorityCode.name() : null;
        logger.info("User {} is retrieving tasks with filters: status={}, priority={}, authorId={}, assigneeId={}",
                user.getUsername(), statusCode, priorityCode, authorId, assigneeId);

        Page<Task> taskList;
        if (user.getRole().is(RoleCode.USER)) {
            taskList = taskRepository.findAllByFiltersAuthorAndAssignee(user.getId(), statusCodeStr, priorityCodeStr, pageable);
        } else {
            taskList = taskRepository.findAllByFilters(statusCodeStr, priorityCodeStr, authorId, assigneeId, pageable);
        }

        logger.info("Retrieved {} tasks successfully", taskList.getTotalElements());
        return taskList.map(TaskConverter::toDto);
    }

    /**
     * Finds a user by ID.
     *
     * @param userId the user ID
     * @return the found user
     * @throws UserNotFoundException if user not found
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    /**
     * Applies common fields from a task DTO to an existing task.
     *
     * @param dto  the DTO containing task fields
     * @param task the task entity to modify
     */
    private void applyDtoToTask(TaskRequestDto dto, Task task) {
        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatusCode() != null) task.setStatus(statusRepository.findByCode(dto.getStatusCode().name()).orElseThrow(() -> new IllegalArgumentException("Invalid status")));
        if (dto.getPriorityCode() != null) task.setPriority(priorityRepository.findByCode(dto.getPriorityCode().name()).orElseThrow(() -> new IllegalArgumentException("Invalid priority")));
    }
}