package ru.effectivemobile.taskmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.effectivemobile.taskmanagement.dto.*;
import ru.effectivemobile.taskmanagement.exceptions.*;
import ru.effectivemobile.taskmanagement.model.*;
import ru.effectivemobile.taskmanagement.repository.*;
import ru.effectivemobile.taskmanagement.service.TaskService;
import ru.effectivemobile.taskmanagement.util.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public void deleteTask(Long id) {
        User user = currentUserProvider.getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));

        if (user.getRole().equals(Role.USER) && !task.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("The user is not the author of the task!");
        }

        taskRepository.deleteById(id);
    }

    @Override
    public TaskResponseDto updateAdminTask(Long taskId, TaskRequestAdminDto dto) {
        User user = currentUserProvider.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        applyDtoToTask(dto, task);

        if (dto.getAssigneeId() != null) {
            User assignee = findUserById(dto.getAssigneeId());
            task.setAssignee(assignee);
        }

        task.setAuthor(user);
        task.setId(taskId);
        taskRepository.save(task);

        return TaskConverter.toDto(task);
    }

    @Override
    public TaskResponseDto updateUserTask(Long taskId, TaskRequestUserDto dto) {
        User user = currentUserProvider.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        boolean isAuthor = task.getAuthor().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee().getId().equals(user.getId());

        if (!isAuthor && !isAssignee) {
            throw new AccessDeniedException("User is neither the author nor the assignee of the task");
        }

        if (isAssignee && !isAuthor && dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }

        if (isAuthor) {
            applyDtoToTask(dto, task);
        }

        task.setId(taskId);
        taskRepository.save(task);

        return TaskConverter.toDto(task);
    }

    @Override
    public TaskResponseDto createAdminTask(TaskRequestAdminDto dto) {
        User user = currentUserProvider.getCurrentUser();

        User assignee = findUserById(dto.getAssigneeId());
        Task task = TaskConverter.toEntity(dto, user, assignee);
        taskRepository.save(task);

        return TaskConverter.toDto(task);
    }

    @Override
    public TaskResponseDto createUserTask(TaskRequestUserDto dto) {
        User user = currentUserProvider.getCurrentUser();

        Task task = TaskConverter.toEntity(dto, user);
        taskRepository.save(task);

        return TaskConverter.toDto(task);
    }

    @Override
    public TaskResponseDto getTask(Long id) {
        User user = currentUserProvider.getCurrentUser();

        Task task;
        if (user.getRole().equals(Role.USER)) {
            task = taskRepository.findByIdAuthorAndAssignee(id, user.getId())
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        } else {
            task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
        }

        return TaskConverter.toDto(task);
    }

    @Override
    public Page<TaskResponseDto> getAllTasks(Status status, Priority priority, Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();

        Page<Task> taskList;
        if (user.getRole().equals(Role.USER)) {
            taskList = taskRepository.findAllByFiltersAuthorAndAssignee(user.getId(), status, priority, pageable);
        } else {
            taskList = taskRepository.findAllByFilters(status, priority, pageable);
        }

        return taskList.map(TaskConverter::toDto);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    private void applyDtoToTask(TaskRequestDto dto, Task task) {
        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
    }
}
