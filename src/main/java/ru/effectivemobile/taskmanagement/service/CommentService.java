package ru.effectivemobile.taskmanagement.service;

import ru.effectivemobile.taskmanagement.dto.CommentRequestDTO;
import ru.effectivemobile.taskmanagement.dto.CommentResponseDTO;

import java.util.List;

/**
 * Service interface for managing comments related to tasks.
 * <p>
 * This service provides operations for creating comments on tasks and retrieving all comments for a specific task.
 * It ensures that only authorized users (task authors, assignees, or admins) can create comments, and it provides a way to retrieve all comments for a given task.
 * </p>
 */
public interface CommentService {

    /**
     * Creates a new comment on a task.
     * <p>
     * Only users who are the task author, assignee, or admins are allowed to create a comment.
     * </p>
     *
     * @param taskId the ID of the task the comment is related to
     * @param dto    the data transfer object containing the comment text and other required information
     * @return the created comment wrapped in a response DTO
     */
    CommentResponseDTO createUserComment(long taskId, CommentRequestDTO dto);

    /**
     * Retrieves all comments associated with a given task.
     * <p>
     * This method retrieves a list of all comments made on a specific task.
     * </p>
     *
     * @param taskId the ID of the task for which comments are being retrieved
     * @return a list of comment response DTOs
     */
    List<CommentResponseDTO> getAllComments(long taskId);
}
