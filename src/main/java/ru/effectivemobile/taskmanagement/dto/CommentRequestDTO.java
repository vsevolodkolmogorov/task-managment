package ru.effectivemobile.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new comment.
 * <p>
 * Contains the textual content of the comment. Validation ensures the text is not blank.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for creating a new comment on a task")
public class CommentRequestDTO {

    /**
     * The textual content of the comment.
     * This field is required and must not be blank.
     */
    @Schema(description = "Text content of the comment", example = "This task needs to be reviewed", required = true)
    @NotBlank(message = "Text of the comment must not be blank")
    private String text;
}
