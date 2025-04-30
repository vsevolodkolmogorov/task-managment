package ru.effectivemobile.taskmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import ru.effectivemobile.taskmanagement.util.TaskConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /**
     * Delete a task by ID. Only the author or an admin can delete a task.
     * @param id The ID of the task to be deleted.
     * @param user The user requesting the deletion.
     */
    @Override
    public void deleteTask(Long id, User user) {
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
     * @param id The ID of the task to update.
     * @param taskRequestDto The DTO containing the updated task data.
     * @return The updated task response DTO.
     */
    @Override
    public TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto) {
        // Find the author and assignee by their IDs. Throw an exception if not found.
        User author = findUserById(taskRequestDto.getAuthorId());
        User assignee = findUserById(taskRequestDto.getAssigneeId());

        // Convert the task request DTO to an entity and set its ID.
        Task task = TaskConverter.toEntity(taskRequestDto, author, assignee);
        task.setId(id);

        // Save the updated task to the database.
        taskRepository.save(task);

        // Return the updated task as a DTO.
        return TaskConverter.toDto(task);
    }

    /**
     * Update a task based on the user's request, ensuring the user is either the author or assignee.
     * @param taskId The ID of the task to update.
     * @param dto The DTO containing the updated task data from the user.
     * @param currentUser The user making the request.
     * @return The updated task response DTO.
     */
    @Override
    public TaskResponseDto updateUserTask(Long taskId, TaskRequestUserDto dto, User currentUser) {
        // Retrieve the task by its ID, throw an exception if not found.
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        // Check if the current user is either the author or assignee of the task.
        boolean isAuthor = task.getAuthor().getId().equals(currentUser.getId());
        boolean isAssignee = task.getAssignee().getId().equals(currentUser.getId());

        if (!isAuthor && !isAssignee) {
            throw new AccessDeniedException("User is neither the author nor the assignee of the task");
        }

        // Depending on the user's role, update the task with the provided data.
        TaskRequestDto updateDto = TaskRequestDto.builder()
                .title(isAssignee ? task.getTitle() : dto.getTitle())
                .description(isAssignee ? task.getDescription() : dto.getDescription())
                .status(dto.getStatus())
                .priority(isAssignee ? task.getPriority() : dto.getPriority())
                .authorId(isAuthor ? currentUser.getId() : task.getAuthor().getId())
                .assigneeId(isAssignee ? task.getAssignee().getId() : currentUser.getId())
                .build();

        // Update and return the task with the new data.
        return updateTask(taskId, updateDto);
    }

    /**
     * Create a new task with the provided task request DTO.
     * @param taskRequestDto The DTO containing the data for the new task.
     * @return The created task response DTO.
     */
    @Override
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        // Find the author and assignee by their IDs. Throw an exception if not found.
        User author = findUserById(taskRequestDto.getAuthorId());
        User assignee = findUserById(taskRequestDto.getAssigneeId());

        // Convert the task request DTO to an entity.
        Task task = TaskConverter.toEntity(taskRequestDto, author, assignee);

        // Save the new task to the database.
        taskRepository.save(task);

        // Return the created task as a DTO.
        return TaskConverter.toDto(task);
    }

    /**
     * Get a task by its ID.
     * @param id The ID of the task to retrieve.
     * @return The task response DTO.
     */
    @Override
    public TaskResponseDto getTask(Long id) {
        // Retrieve the task by ID, throw an exception if not found.
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        return TaskConverter.toDto(task);
    }

    /**
     * Get a user's task by task ID and user ID, checking that the user is either the author or assignee.
     * @param id The task ID.
     * @param userId The user ID.
     * @return The task response DTO.
     */
    @Override
    public TaskResponseDto getUserTask(Long id, Long userId) {
        // Retrieve the task by both the task ID and user ID, throw an exception if not found.
        Task task = taskRepository.findByIdAuthorAndAssignee(id, userId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));

        return TaskConverter.toDto(task);
    }

    /**
     * Get all tasks.
     * @return A list of all task response DTOs.
     */
    @Override
    public List<TaskResponseDto> getAllTasks() {
        // Retrieve all tasks from the database.
        List<Task> taskList = taskRepository.findAll();

        // Convert each task to a response DTO and return the list.
        return taskList.stream()
                .map(TaskConverter::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all tasks for a specific user.
     * @param id The user ID.
     * @return A list of task response DTOs assigned to the user or authored by them.
     */
    @Override
    public List<TaskResponseDto> getAllUsersTasks(Long id) {
        // Retrieve all tasks for a user, throw an exception if not found.
        List<Task> taskList = taskRepository.findAllByAuthorAndAssignee(id)
                .orElseThrow(() -> new TaskNotFoundException("No tasks found for user with id " + id));

        // Convert each task to a response DTO and return the list.
        return taskList.stream()
                .map(TaskConverter::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to find a user by their ID.
     * @param userId The ID of the user to find.
     * @return The user.
     * @throws UserNotFoundException If the user is not found.
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }
}
