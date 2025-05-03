package ru.effectivemobile.taskmanagement.model;

/**
 * Enum representing the status of a task in the system.
 * <p>
 * Defines the various stages of a task's lifecycle, indicating its current state.
 * </p>
 */
public enum Status {

    /**
     * Task is created but not yet started.
     * Typically used for tasks that are awaiting action.
     */
    PENDING,

    /**
     * Task is currently being worked on.
     * Indicates that the task is in progress and requires attention.
     */
    IN_PROGRESS,

    /**
     * Task has been completed.
     * Used when the task is finished and all required actions are done.
     */
    COMPLETED
}
