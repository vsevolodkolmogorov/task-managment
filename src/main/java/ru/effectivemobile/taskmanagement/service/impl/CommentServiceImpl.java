package ru.effectivemobile.taskmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.effectivemobile.taskmanagement.dto.CommentRequestDTO;
import ru.effectivemobile.taskmanagement.dto.CommentResponseDTO;
import ru.effectivemobile.taskmanagement.exceptions.AccessDeniedException;
import ru.effectivemobile.taskmanagement.exceptions.NullTextCommentException;
import ru.effectivemobile.taskmanagement.exceptions.TaskNotFoundException;
import ru.effectivemobile.taskmanagement.model.Comment;
import ru.effectivemobile.taskmanagement.model.Role;
import ru.effectivemobile.taskmanagement.model.Task;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.model.enums.RoleCode;
import ru.effectivemobile.taskmanagement.repository.CommentRepository;
import ru.effectivemobile.taskmanagement.repository.TaskRepository;
import ru.effectivemobile.taskmanagement.service.CommentService;
import ru.effectivemobile.taskmanagement.util.CommentConverter;
import ru.effectivemobile.taskmanagement.util.CurrentUserProvider;

import java.util.List;

/**
 * Implementation of the CommentService interface that handles business logic related to task comments.
 * <p>
 * Supports creation and retrieval of comments, with access control based on user roles and task ownership.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CurrentUserProvider currentUserProvider;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    /**
     * Creates a comment for a specific task by the current authenticated user.
     *
     * @param taskId the ID of the task the comment is associated with
     * @param dto    the request payload containing comment text
     * @return the created comment wrapped in a response DTO
     * @throws NullTextCommentException if the comment text is empty or blank
     * @throws TaskNotFoundException if the task with the specified ID does not exist
     * @throws AccessDeniedException if the current user is not the task author, assignee, or admin
     */
    @Override
    public CommentResponseDTO createUserComment(long taskId, CommentRequestDTO dto) {
        User user = currentUserProvider.getCurrentUser();

        if (dto.getText().isEmpty() || dto.getText().isBlank()) {
            logger.error("Attempt to create comment with empty or null text. Task ID: {}", taskId);
            throw new NullTextCommentException("Text of the comment is null or empty!");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.error("Task with ID {} not found", taskId);
                    return new TaskNotFoundException("Task with id " + taskId + " not found");
                });

        boolean isAuthor = task.getAuthor().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee().getId().equals(user.getId());

        if (!user.getRole().is(RoleCode.ADMIN) && !(isAuthor || isAssignee)) {
            logger.warn("User with ID {} attempted to comment on task ID {} without sufficient privileges", user.getId(), taskId);
            throw new AccessDeniedException("User is not author or assignee of task!");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .user(user)
                .task(task)
                .build();

        Comment savedComment = commentRepository.save(comment);
        logger.info("Created comment with ID {} for task ID {}", savedComment.getId(), taskId);
        return CommentConverter.toDto(savedComment);
    }

    /**
     * Retrieves all comments associated with a given task.
     *
     * @param taskId the ID of the task
     * @return a list of comment DTOs associated with the task
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public List<CommentResponseDTO> getAllComments(long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.error("Task with ID {} not found", taskId);
                    return new TaskNotFoundException("Task with id " + taskId + " not found");
                });

        List<Comment> commentList = commentRepository.findAllByTaskId(task.getId());
        logger.info("Retrieved {} comments for task ID {}", commentList.size(), taskId);

        return commentList.stream()
                .map(CommentConverter::toDto)
                .toList();
    }
}