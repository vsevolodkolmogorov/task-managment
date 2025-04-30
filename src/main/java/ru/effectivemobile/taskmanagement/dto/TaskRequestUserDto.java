package ru.effectivemobile.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.effectivemobile.taskmanagement.model.Priority;
import ru.effectivemobile.taskmanagement.model.Status;

/**
 * DTO used by regular users to create or update a task.
 * This version of the task DTO does not allow specifying the assignee manually,
 * as regular users can only assign tasks to themselves.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskRequestUserDto {

    /**
     * Title of the task.
     * Example: "Implement user login"
     */
    @Schema(description = "Title of the task", example = "Implement user login")
    private String title;

    /**
     * Detailed description of the task.
     * Example: "Create login form, validate inputs, and integrate with backend API"
     */
    @Schema(description = "Detailed description of the task", example = "Create login form, validate inputs, and integrate with backend API")
    private String description;

    /**
     * Priority level of the task (e.g., LOW, MEDIUM, HIGH).
     * Example: "HIGH"
     */
    @Schema(description = "Priority level of the task (e.g., LOW, MEDIUM, HIGH)", example = "HIGH")
    private Priority priority;

    /**
     * Current status of the task (e.g., PENDING, IN_PROGRESS, COMPLETED).
     * Example: "PENDING"
     */
    @Schema(description = "Current status of the task (e.g., PENDING, IN_PROGRESS, COMPLETED)", example = "PENDING")
    private Status status;
}

