package ru.effectivemobile.taskmanagement.exceptions;

/**
 * Exception thrown when a user is not found in the system.
 * <p>
 * This exception is typically thrown when the system attempts to retrieve a user
 * by their identifier (e.g., email or ID), but the user does not exist in the database.
 * </p>
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param text The detail message to be used for this exception, typically describing the reason for the exception.
     */
    public UserNotFoundException(String text) {
        super(text);
    }
}
