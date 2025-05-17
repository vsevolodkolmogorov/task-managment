package ru.effectivemobile.taskmanagement.dto;

import ru.effectivemobile.taskmanagement.model.Priority;
import ru.effectivemobile.taskmanagement.model.enums.StatusCode;

/**
 * Interface representing the data transfer object (DTO) for a task request.
 * <p>
 * This interface defines the common methods that should be implemented by any class
 * that represents the data for creating or updating a task.
 * </p>
 */
public interface TaskRequestDto {

    /**
     * Gets the title of the task.
     *
     * @return the title of the task
     */
    String getTitle();

    /**
     * Gets the description of the task.
     *
     * @return the description of the task
     */
    String getDescription();

    /**
     * Gets the status of the task.
     *
     * @return the status of the task (e.g., "OPEN", "IN_PROGRESS", "COMPLETED")
     */
    StatusCode getStatusCode();

    /**
     * Gets the priority of the task.
     *
     * @return the priority of the task (e.g., "HIGH", "MEDIUM", "LOW")
     */
    Priority getPriority();
}
