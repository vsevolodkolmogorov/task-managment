package ru.effectivemobile.taskmanagement.service;

import ru.effectivemobile.taskmanagement.dto.TaskRequestAdminDto;
import ru.effectivemobile.taskmanagement.dto.TaskRequestUserDto;
import ru.effectivemobile.taskmanagement.dto.TaskResponseDto;

import java.util.List;

/**
 * Service interface for managing tasks within the task management system.
 * <p>
 * This service interface defines methods for creating, updating, deleting,
 * retrieving tasks, and handling user-specific tasks. It includes both
 * administrative functionality and user-specific task management.
 * </p>
 */
public interface TaskService {

    /**
     * Deletes a task by its ID.
     * <p>
     * This method allows an admin or the task author to delete a specific task.
     * </p>
     *
     * @param id the ID of the task to delete
     */
    void deleteTask(Long id);

    /**
     * Updates a task based on the provided task request.
     * <p>
     * This method allows updating of task details (e.g., title, description, status, etc.)
     * </p>
     *
     * @param id the ID of the task to update
     * @param taskRequestAdminDto the request DTO containing the updated task details
     * @return the updated task as a response DTO
     */
    TaskResponseDto updateAdminTask(Long id, TaskRequestAdminDto taskRequestAdminDto);

    /**
     * Updates a task assigned to a user.
     * <p>
     * This method allows users to update tasks they are assigned to, typically for task status
     * or progress updates.
     * </p>
     *
     * @param id the ID of the task to update
     * @param taskRequestDto the request DTO containing the updated task details
     * @return the updated task as a response DTO
     */
    TaskResponseDto updateUserTask(Long id, TaskRequestUserDto taskRequestDto);

    /**
     * Creates a new task for Admin.
     * <p>
     * This method allows the creation of a new task by an admin or the task's author.
     * </p>
     *
     * @param taskRequestAdminDto the request DTO containing the task details to create
     * @return the created task as a response DTO
     */
    TaskResponseDto createAdminTask(TaskRequestAdminDto taskRequestAdminDto);

    /**
     * Creates a new task for User.
     * <p>
     * This method allows the creation of a new task only by user.
     * </p>
     *
     * @param taskRequestUserDto the request DTO containing the task details to create
     * @return the created task as a response DTO
     */
    TaskResponseDto createUserTask(TaskRequestUserDto taskRequestUserDto);


    /**
     * Retrieves a task assigned to a specific user.
     * <p>
     * This method retrieves a task by its ID that is associated with a specific user.
     * </p>
     *
     * @param id the ID of the task to retrieve
     * @return the task details as a response DTO
     */
    TaskResponseDto getTask(Long id);

    /**
     * Retrieves all tasks.
     * <p>
     * This method retrieves all tasks in the system, typically used for admin functionalities.
     * </p>
     *
     * @return a list of all tasks as response DTOs
     */
    List<TaskResponseDto> getAllTasks();}
