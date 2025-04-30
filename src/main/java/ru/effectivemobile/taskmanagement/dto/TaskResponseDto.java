package ru.effectivemobile.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.effectivemobile.taskmanagement.model.Priority;
import ru.effectivemobile.taskmanagement.model.Status;

/**
 * Data Transfer Object for returning detailed task information.
 * <p>
 * This DTO is used in API responses to provide complete task metadata,
 * including the author and assignee's basic identification.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskResponseDto {

    /**
     * Unique identifier of the task.
     * Example: 1001
     */
    @Schema(description = "Unique identifier of the task", example = "1001")
    private Long id;

    /**
     * Title or short name of the task.
     * Example: "Implement registration flow"
     */
    @Schema(description = "Title of the task", example = "Implement registration flow")
    private String title;

    /**
     * Detailed description explaining the task requirements.
     * Example: "Add registration form, handle backend integration, and validate inputs"
     */
    @Schema(description = "Detailed description of the task", example = "Add registration form, handle backend integration, and validate inputs")
    private String description;

    /**
     * Priority level assigned to the task.
     * Can be LOW, MEDIUM, or HIGH.
     * Example: "MEDIUM"
     */
    @Schema(description = "Priority level of the task (e.g., LOW, MEDIUM, HIGH)", example = "MEDIUM")
    private Priority priority;

    /**
     * Current status of the task lifecycle.
     * Common values: TODO, IN_PROGRESS, DONE.
     * Example: "IN_PROGRESS"
     */
    @Schema(description = "Current status of the task (e.g., TODO, IN_PROGRESS, DONE)", example = "IN_PROGRESS")
    private Status status;

    /**
     * User ID of the person who created the task.
     * Example: 101
     */
    @Schema(description = "ID of the user who created the task", example = "101")
    private Long authorId;

    /**
     * Email of the task's author.
     * Example: "author@example.com"
     */
    @Schema(description = "Email of the task's author", example = "author@example.com")
    private String authorEmail;

    /**
     * User ID of the person assigned to the task.
     * Example: 205
     */
    @Schema(description = "ID of the user assigned to the task", example = "205")
    private Long assigneeId;

    /**
     * Email of the user who is assigned to the task.
     * Example: "assignee@example.com"
     */
    @Schema(description = "Email of the assigned user", example = "assignee@example.com")
    private String assigneeEmail;
}
