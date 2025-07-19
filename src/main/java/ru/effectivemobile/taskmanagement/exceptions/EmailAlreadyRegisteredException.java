package ru.effectivemobile.taskmanagement.exceptions;

/**
 * Custom exception thrown when a user tries to register an email that is already
 * registered in the system.
 * <p>
 * This exception is typically used during user registration processes to signal
 * that the provided email address is already associated with an existing account.
 * </p>
 */
public class EmailAlreadyRegisteredException extends RuntimeException {

    /**
     * Constructs a new {@code EmailAlreadyRegisteredException} with the specified detail message.
     * <p>
     * The message provides details about the email that is already registered.
     * This message is passed to the superclass {@link RuntimeException}.
     * </p>
     *
     * @param text the detail message explaining the issue with the email
     */
    public EmailAlreadyRegisteredException(String text) {
        super(text);
    }
}
