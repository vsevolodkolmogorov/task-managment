package ru.effectivemobile.taskmanagement.exceptions;

/**
 * Exception thrown when a task is not found in the system.
 * <p>
 * This exception is typically thrown when the system attempts to retrieve a task
 * by its ID, but the task does not exist in the database.
 * </p>
 */
public class TaskNotFoundException extends RuntimeException {

    /**
     * Constructs a new TaskNotFoundException with the specified detail message.
     *
     * @param text The detail message to be used for this exception, typically describing the reason for the exception.
     */
    public TaskNotFoundException(String text) {
        super(text);
    }
}
