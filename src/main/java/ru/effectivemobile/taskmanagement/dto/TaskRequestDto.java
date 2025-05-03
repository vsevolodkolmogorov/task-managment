package ru.effectivemobile.taskmanagement.dto;

import ru.effectivemobile.taskmanagement.model.Priority;
import ru.effectivemobile.taskmanagement.model.Status;

public interface TaskRequestDto {
    String getTitle();
    String getDescription();
    Status getStatus();
    Priority getPriority();
}
