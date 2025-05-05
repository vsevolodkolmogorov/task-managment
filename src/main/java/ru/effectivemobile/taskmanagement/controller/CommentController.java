package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.taskmanagement.dto.CommentRequestDTO;
import ru.effectivemobile.taskmanagement.dto.CommentResponseDTO;
import ru.effectivemobile.taskmanagement.service.CommentService;

import java.util.List;

/**
 * REST controller for managing comments related to tasks.
 * <p>
 * Provides endpoints to create and retrieve comments associated with specific tasks.
 * All endpoints require JWT authentication with roles USER or ADMIN.
 * </p>
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Endpoints for creating and retrieving comments on tasks")
public class CommentController {

    private final CommentService commentService;

    /**
     * Create a new comment on a specific task.
     *
     * @param dto    The comment request data (must include non-empty text).
     * @param taskId The ID of the task to comment on.
     * @return A response entity containing the created comment data and HTTP status 201.
     */
    @Operation(
            summary = "Create comment",
            description = "Allows users with role USER or ADMIN to add a comment to a specific task."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created successfully",
                    content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid comment data (e.g. blank text)",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not authorized to comment on this task",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CommentResponseDTO> createComment(
            @Valid @RequestBody CommentRequestDTO dto,
            @Parameter(description = "ID of the task to comment on", required = true)
            @PathVariable long taskId
    ) {
        CommentResponseDTO created = commentService.createUserComment(taskId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieve all comments associated with a specific task.
     *
     * @param taskId The ID of the task.
     * @return A list of all comments for the given task.
     */
    @Operation(
            summary = "Get all comments for a task",
            description = "Returns a list of all comments attached to a specific task. Accessible to USER and ADMIN roles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments",
                    content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CommentResponseDTO>> getAllTasks(
            @Parameter(description = "ID of the task whose comments are being retrieved", required = true)
            @PathVariable long taskId
    ) {
        return ResponseEntity.ok(commentService.getAllComments(taskId));
    }
}
