package ru.effectivemobile.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a comment returned in responses.
 * <p>
 * Contains information about the comment's content, author, associated task, and timestamps.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response payload containing detailed information about a task comment")
public class CommentResponseDTO {

    @Schema(description = "Unique identifier of the comment", example = "42")
    private Long id;

    @Schema(description = "Text content of the comment", example = "Please address this issue before the deadline.")
    private String text;

    @Schema(description = "ID of the user who authored the comment", example = "7")
    private Long authorId;

    @Schema(description = "Email of the user who authored the comment", example = "user@example.com")
    private String authorEmail;

    @Schema(description = "ID of the task to which the comment belongs", example = "101")
    private Long taskId;

    @Schema(description = "Timestamp when the comment was created", example = "2024-05-01T14:23:45")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the comment was last updated", example = "2024-05-02T10:00:00")
    private LocalDateTime updatedAt;
}
