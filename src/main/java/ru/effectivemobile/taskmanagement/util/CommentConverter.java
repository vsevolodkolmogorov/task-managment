package ru.effectivemobile.taskmanagement.util;

import ru.effectivemobile.taskmanagement.dto.CommentResponseDTO;
import ru.effectivemobile.taskmanagement.model.Comment;

public class CommentConverter {

    public static CommentResponseDTO toDto(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();

        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setTaskId(comment.getTask().getId());
        dto.setAuthorEmail(comment.getUser().getEmail());
        dto.setAuthorId(comment.getUser().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        // Return the populated DTO.
        return dto;
    }
}
