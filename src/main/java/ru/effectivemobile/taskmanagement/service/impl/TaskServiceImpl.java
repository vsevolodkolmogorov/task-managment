package ru.effectivemobile.taskmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.effectivemobile.taskmanagement.dto.TaskRequestAdminDto;
import ru.effectivemobile.taskmanagement.dto.TaskRequestDto;
import ru.effectivemobile.taskmanagement.dto.TaskRequestUserDto;
import ru.effectivemobile.taskmanagement.dto.TaskResponseDto;
import ru.effectivemobile.taskmanagement.exceptions.AccessDeniedException;
import ru.effectivemobile.taskmanagement.exceptions.TaskNotFoundException;
import ru.effectivemobile.taskmanagement.exceptions.UserNotFoundException;
import ru.effectivemobile.taskmanagement.model.Role;
import ru.effectivemobile.taskmanagement.model.Task;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.repository.TaskRepository;
import ru.effectivemobile.taskmanagement.repository.UserRepository;
import ru.effectivemobile.taskmanagement.service.TaskService;
import ru.effectivemobile.taskmanagement.util.CurrentUserProvider;
import ru.effectivemobile.taskmanagement.util.TaskConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Delete a task by ID. Only the author or an admin can delete a task.
     *
     * @param id   The ID of the task to be deleted.
     */
    @Override
    public void deleteTask(Long id) {
        User user = currentUserProvider.getCurrentUser();
        // Retrieve the task by ID, throw an exception if not found.
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));

        // If the user is a regular user, they can only delete tasks they created.
        if (user.getRole().equals(Role.USER) && !task.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("The user is not the author of the task!");
        }

        // If the user is an admin or the author, delete the task.
        taskRepository.deleteById(id);
    }

    /**
     * Update a task based on the provided task request DTO.
     *
     * @param dto The DTO containing the updated task data.
     * @return The updated task response DTO.
     */
    @Override
    public TaskResponseDto updateAdminTask(Long taskId, TaskRequestAdminDto dto) {
        User user = currentUserProvider.getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        // Find the author and assignee by their IDs. Throw an exception if not found.
        applyDtoToTask(dto, task);

        if (dto.getAssigneeId() != null) {
            User assignee = findUserById(dto.getAssigneeId());
            task.setAssignee(assignee);
        }

        task.setAuthor(user);
        task.setId(taskId);

        // Save the updated task to the database.
        taskRepository.save(task);

        // Return the updated task as a DTO.
        return TaskConverter.toDto(task);
    }

    /**
     * Update a task based on the user's request, ensuring the user is either the author or assignee.
     *
     * @param taskId      The ID of the task to update.
     * @param dto         The DTO containing the updated task data from the user.
     * @return The updated task response DTO.
     */
    @Override
    public TaskResponseDto updateUserTask(Long taskId, TaskRequestUserDto dto) {
        User user = currentUserProvider.getCurrentUser();
        // Retrieve the task by its ID, throw an exception if not found.
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        // Check if the current user is either the author or assignee of the task.
        boolean isAuthor = task.getAuthor().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee().getId().equals(user.getId());

        if (!isAuthor && !isAssignee) {
            throw new AccessDeniedException("User is neither the author nor the assignee of the task");
        }

        if (isAssignee && !isAuthor) {
            if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        }

        if (isAuthor) applyDtoToTask(dto, task);

        task.setId(taskId);

        // Save the updated task to the database.
        taskRepository.save(task);

        // Return the updated task as a DTO.
        return TaskConverter.toDto(task);
    }

    /**
     * Create a new task with the provided task request DTO.
     *
     * @param taskRequestAdminDto The DTO containing the data for the new task.
     * @return The created task response DTO.
     */
    @Override
    public TaskResponseDto createAdminTask(TaskRequestAdminDto taskRequestAdminDto) {
        User user = currentUserProvider.getCurrentUser();

        // Find the author and assignee by their IDs. Throw an exception if not found.
        User assignee = findUserById(taskRequestAdminDto.getAssigneeId());

        // Convert the task request DTO to an entity.
        Task task = TaskConverter.toEntity(taskRequestAdminDto, user, assignee);

        // Save the new task to the database.
        taskRepository.save(task);

        // Return the created task as a DTO.
        return TaskConverter.toDto(task);
    }

    /**
     * Create a new task with the provided task request DTO.
     *
     * @param taskRequestUserDto The DTO containing the data for the new task.
     * @return The created task response DTO.
     */
    @Override
    public TaskResponseDto createUserTask(TaskRequestUserDto taskRequestUserDto) {
        User user = currentUserProvider.getCurrentUser();
        // Convert the task request DTO to an entity.
        Task task = TaskConverter.toEntity(taskRequestUserDto, user);

        // Save the new task to the database.
        taskRepository.save(task);

        // Return the created task as a DTO.
        return TaskConverter.toDto(task);
    }

    /**
     * Get a task by its ID.
     *
     * @param id The ID of the task to retrieve.
     * @return The task response DTO.
     */
    @Override
    public TaskResponseDto getTask(Long id) {
        User user = currentUserProvider.getCurrentUser();
        Task task;

        if (user.getRole().equals(Role.USER)) {
            // Retrieve the task by both the task ID and user ID, throw an exception if not found.
            task = taskRepository.findByIdAuthorAndAssignee(id, user.getId())
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));

        } else {
            // Retrieve the task by ID, throw an exception if not found.
            task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        }

        return TaskConverter.toDto(task);
    }

    /**
     * Get all tasks.
     *
     * @return A list of all task response DTOs.
     */
    @Override
    public List<TaskResponseDto> getAllTasks() {
        User user = currentUserProvider.getCurrentUser();
        List<Task> taskList = taskRepository.findAll();;

        if (user.getRole().equals(Role.USER)) {
            // Retrieve all tasks for a user, throw an exception if not found.
            taskList = taskRepository.findAllByAuthorAndAssignee(user.getId())
                    .orElseThrow(() -> new TaskNotFoundException("No tasks found for user with id " + user.getId()));
        }

        // Convert each task to a response DTO and return the list.
        return taskList.stream()
                .map(TaskConverter::toDto)
                .collect(Collectors.toList());
    }


    /**
     * Helper method to find a user by their ID.
     *
     * @param userId The ID of the user to find.
     * @return The user.
     * @throws UserNotFoundException If the user is not found.
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    private void applyDtoToTask(TaskRequestDto dto, Task task) {
        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
    }

}
