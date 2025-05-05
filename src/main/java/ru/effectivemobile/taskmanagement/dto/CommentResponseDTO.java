package ru.effectivemobile.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a comment returned in responses.
 * <p>
 * This DTO contains the essential information related to a comment, including:
 * - The unique identifier of the comment
 * - The text content of the comment
 * - The author's ID and email
 * - The ID of the associated task
 * - Timestamps for when the comment was created and last updated
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response payload containing detailed information about a task comment")
public class CommentResponseDTO {

    /**
     * Unique identifier of the comment.
     * <p>
     * This ID is used to uniquely identify a comment in the system.
     * </p>
     */
    @Schema(description = "Unique identifier of the comment", example = "42")
    private Long id;

    /**
     * Text content of the comment.
     * <p>
     * This is the actual message or note written by the author regarding the task.
     * </p>
     */
    @Schema(description = "Text content of the comment", example = "Please address this issue before the deadline.")
    private String text;

    /**
     * ID of the user who authored the comment.
     * <p>
     * This ID corresponds to the user who created the comment in the system.
     * </p>
     */
    @Schema(description = "ID of the user who authored the comment", example = "7")
    private Long authorId;

    /**
     * Email of the user who authored the comment.
     * <p>
     * This is the email address of the user who wrote the comment.
     * </p>
     */
    @Schema(description = "Email of the user who authored the comment", example = "user@example.com")
    private String authorEmail;

    /**
     * ID of the task to which the comment belongs.
     * <p>
     * This ID links the comment to a specific task in the system.
     * </p>
     */
    @Schema(description = "ID of the task to which the comment belongs", example = "101")
    private Long taskId;

    /**
     * Timestamp when the comment was created.
     * <p>
     * This field stores the date and time when the comment was originally created.
     * </p>
     */
    @Schema(description = "Timestamp when the comment was created", example = "2024-05-01T14:23:45")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the comment was last updated.
     * <p>
     * This field stores the date and time when the comment was last modified.
     * </p>
     */
    @Schema(description = "Timestamp when the comment was last updated", example = "2024-05-02T10:00:00")
    private LocalDateTime updatedAt;
}