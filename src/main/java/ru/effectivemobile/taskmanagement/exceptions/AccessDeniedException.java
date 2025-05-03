package ru.effectivemobile.taskmanagement.exceptions;

/**
 * Custom exception thrown when a user tries to access a resource
 * or perform an action that they do not have permission for.
 * <p>
 * This exception is used to indicate a lack of proper authorization
 * for performing a specific action within the system.
 * </p>
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Constructs a new {@code AccessDeniedException} with the specified detail message.
     * <p>
     * This message will be passed to the superclass {@link RuntimeException}
     * and can be used to provide more information about the specific
     * access violation.
     * </p>
     *
     * @param text the detail message explaining the access violation
     */
    public AccessDeniedException(String text) {
        super(text);
    }
}
