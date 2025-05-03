package ru.effectivemobile.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.effectivemobile.taskmanagement.model.Priority;
import ru.effectivemobile.taskmanagement.model.Status;
import ru.effectivemobile.taskmanagement.validation.OnCreate;
import ru.effectivemobile.taskmanagement.validation.OnUpdate;

/**
 * Data Transfer Object for creating or updating a task.
 * <p>
 * This DTO is used to send task-related information when creating or updating a task.
 * It includes essential metadata such as the task's title, description, priority, status,
 * and user assignments for both the author and the assignee.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskRequestAdminDto implements TaskRequestDto {

    /**
     * Title of the task that summarizes its objective.
     * Example: "Implement user login"
     */
    @Schema(description = "Title of the task", example = "Implement user login")
    @NotBlank(message = "Title must not be blank", groups = OnCreate.class)
    private String title;

    /**
     * Detailed description of the task explaining the necessary steps.
     * Example: "Create login form, validate inputs, and integrate with backend API"
     */
    @Schema(description = "Detailed description of the task", example = "Create login form, validate inputs, and integrate with backend API")
    @Size(max = 500, groups = {OnCreate.class, OnUpdate.class})
    private String description;

    /**
     * Priority level assigned to the task, which indicates its importance.
     * Possible values: LOW, MEDIUM, HIGH.
     * Example: "HIGH"
     */
    @Schema(description = "Priority level of the task (e.g., LOW, MEDIUM, HIGH)", example = "HIGH")
    @NotNull(message = "Priority must not be blank", groups = OnCreate.class)
    private Priority priority;

    /**
     * Current status of the task in its lifecycle.
     * Common values: PENDING, IN_PROGRESS, COMPLETED.
     * Example: "PENDING"
     */
    @Schema(description = "Current status of the task (e.g., PENDING, IN_PROGRESS, COMPLETED)", example = "PENDING")
    @NotNull(message = "Status must not be null", groups = OnCreate.class)
    @NotBlank(message = "Status must not be blank", groups = OnCreate.class)
    private Status status;

    /**
     * ID of the user assigned to the task.
     * Example: "205"
     */
    @Schema(description = "ID of the user assigned to the task", example = "205")
    @NotNull(message = "AssigneeId must not be null" , groups = OnCreate.class)
    @NotBlank(message = "AssigneeId must not be blank" , groups = OnCreate.class)
    private Long assigneeId;
}
