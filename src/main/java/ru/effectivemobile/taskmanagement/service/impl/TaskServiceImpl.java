package ru.effectivemobile.taskmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.effectivemobile.taskmanagement.dto.*;
import ru.effectivemobile.taskmanagement.exceptions.*;
import ru.effectivemobile.taskmanagement.model.*;
import ru.effectivemobile.taskmanagement.repository.*;
import ru.effectivemobile.taskmanagement.service.TaskService;
import ru.effectivemobile.taskmanagement.util.*;


/**
 * Service implementation for managing tasks.
 * Provides business logic for task CRUD operations, including role-based access control.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

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

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));

        if (user.getRole().equals(Role.USER) && !task.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("The user is not the author of the task!");
        }

        taskRepository.deleteById(id);
    }

    /**
     * Updates an existing task with full admin rights.
     * Admins can modify any task fields and assign any user.
     *
     * @param taskId the ID of the task to update
     * @param dto the data for updating the task
     * @return the updated task as a DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public TaskResponseDto updateAdminTask(Long taskId, TaskRequestAdminDto dto) {
        User user = currentUserProvider.getCurrentUser();

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

        return TaskConverter.toDto(task);
    }

    /**
     * Updates a task with user-level access control.
     * - Author can update title, description, status, priority.
     * - Assignee can only update the status.
     *
     * @param taskId the ID of the task to update
     * @param dto the data for updating the task
     * @return the updated task as a DTO
     * @throws TaskNotFoundException if the task does not exist
     * @throws AccessDeniedException if the user is neither author nor assignee
     */
    @Override
    public TaskResponseDto updateUserTask(Long taskId, TaskRequestUserDto dto) {
        User user = currentUserProvider.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        boolean isAuthor = task.getAuthor().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee().getId().equals(user.getId());

        if (!isAuthor && !isAssignee) {
            throw new AccessDeniedException("User is neither the author nor the assignee of the task");
        }

        if (isAssignee && !isAuthor && dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }

        if (isAuthor) {
            applyDtoToTask(dto, task);
        }

        task.setId(taskId);
        taskRepository.save(task);

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

        User assignee = findUserById(dto.getAssigneeId());
        Task task = TaskConverter.toEntity(dto, user, assignee);
        taskRepository.save(task);

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

        Task task = TaskConverter.toEntity(dto, user);
        taskRepository.save(task);

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

        Task task;
        if (user.getRole().equals(Role.USER)) {
            task = taskRepository.findByIdAuthorAndAssignee(id, user.getId())
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        } else {
            task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        }

        return TaskConverter.toDto(task);
    }

    /**
     * Retrieves a paginated list of tasks.
     * - Users can see only their own tasks.
     * - Admins can filter by authorId and assigneeId.
     *
     * @param status optional filter by status
     * @param priority optional filter by priority
     * @param authorId optional filter (admin only)
     * @param assigneeId optional filter (admin only)
     * @param pageable pagination info
     * @return a page of task DTOs
     */
    @Override
    public Page<TaskResponseDto> getAllTasks(Status status, Priority priority, Long authorId, Long assigneeId, Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();

        Page<Task> taskList;
        if (user.getRole().equals(Role.USER)) {
            taskList = taskRepository.findAllByFiltersAuthorAndAssignee(user.getId(), status, priority, pageable);
        } else {
            taskList = taskRepository.findAllByFilters(status, priority, authorId, assigneeId, pageable);
        }

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
     * @param dto the DTO containing task fields
     * @param task the task entity to modify
     */
    private void applyDtoToTask(TaskRequestDto dto, Task task) {
        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
    }
}
