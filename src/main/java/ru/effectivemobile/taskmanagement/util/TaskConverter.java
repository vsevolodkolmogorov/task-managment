package ru.effectivemobile.taskmanagement.util;

import ru.effectivemobile.taskmanagement.dto.TaskRequestAdminDto;
import ru.effectivemobile.taskmanagement.dto.TaskRequestUserDto;
import ru.effectivemobile.taskmanagement.dto.TaskResponseDto;
import ru.effectivemobile.taskmanagement.model.Status;
import ru.effectivemobile.taskmanagement.model.Task;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.model.enums.StatusCode;

public class TaskConverter {
    /**
     * Converts a TaskRequestDto object into a Task entity.
     *
     * @param dto       The TaskRequestDto object containing the task data.
     * @param admin    The author of the task (User object).
     * @param assignee  The assignee of the task (User object).
     * @return          The Task entity built from the provided DTO and user objects.
     */
    public static Task toEntity(TaskRequestAdminDto dto, User admin, User assignee, Status status) {
        return Task.builder()
                .title(dto.getTitle())            // Set the title from the DTO.
                .description(dto.getDescription()) // Set the description from the DTO.
                .status(status)           // Set the status from the DTO.
                .priority(dto.getPriority())       // Set the priority from the DTO.
                .author(admin)                    // Set the author from the provided User object.
                .assignee(assignee)                // Set the assignee from the provided User object.
                .build();                          // Build the Task entity.
    }

    public static Task toEntity(TaskRequestUserDto dto, User user, Status status) {
        return Task.builder()
                .title(dto.getTitle())            // Set the title from the DTO.
                .description(dto.getDescription()) // Set the description from the DTO.
                .status(status)           // Set the status from the DTO.
                .priority(dto.getPriority())       // Set the priority from the DTO.
                .author(user)                    // Set the author from the provided User object.
                .assignee(user)                // Set the assignee from the provided User object.
                .build();                          // Build the Task entity.
    }

    /**
     * Converts a Task entity to a TaskResponseDto object.
     *
     * @param task  The Task entity to convert.
     * @return      A TaskResponseDto containing the task data.
     */
    public static TaskResponseDto toDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();

        // Set basic task details from the Task entity.
        dto.setId(task.getId());                    // Set the task ID.
        dto.setTitle(task.getTitle());              // Set the task title.
        dto.setDescription(task.getDescription());  // Set the task description.

        try {
            dto.setStatusCode(StatusCode.valueOf(task.getStatus().getCode()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown status code: " + task.getStatus().getCode());
        }

        dto.setPriority(task.getPriority());        // Set the task priority.
        dto.setCommentList(task.getCommentList() != null ? task.getCommentList().stream().map(CommentConverter::toDto).toList() : null); // Set the task comments.
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        // Set author details if the author is not null.
        if (task.getAuthor() != null) {
            dto.setAuthorId(task.getAuthor().getId());           // Set the author's ID.
            dto.setAuthorEmail(task.getAuthor().getEmail());     // Set the author's email.
        }

        // Set assignee details if the assignee is not null.
        if (task.getAssignee() != null) {
            dto.setAssigneeId(task.getAssignee().getId());         // Set the assignee's ID.
            dto.setAssigneeEmail(task.getAssignee().getEmail());   // Set the assignee's email.
        }

        // Return the populated DTO.
        return dto;
    }
}
