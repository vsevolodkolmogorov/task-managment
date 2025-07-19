package ru.effectivemobile.taskmanagement.service;

import ru.effectivemobile.taskmanagement.dto.AuthResponseDTO;
import ru.effectivemobile.taskmanagement.dto.LoginRequestDTO;
import ru.effectivemobile.taskmanagement.dto.RegisterRequestDTO;
import ru.effectivemobile.taskmanagement.model.User;

/**
 * Service interface for handling authentication-related operations.
 * <p>
 * Defines methods for registering and logging in users, providing JWT tokens
 * for authentication and authorization.
 * </p>
 */
public interface AuthService {

    /**
     * Registers a new user by creating a new account with the provided credentials.
     * <p>
     * This method will validate the user input, ensure that the email is not already
     * registered, and create the user's account. If successful, it will return an
     * authentication token.
     * </p>
     *
     * @param request the registration details including email and password
     * @return an authentication response containing a JWT token
     */
    AuthResponseDTO register(RegisterRequestDTO request);

    /**
     * Authenticates a user with the provided credentials and returns an authentication token.
     * <p>
     * This method will validate the user's credentials and return a JWT token if the
     * credentials are correct. If authentication fails, an exception will be thrown.
     * </p>
     *
     * @param request the login details including email and password
     * @return an authentication response containing a JWT token
     */
    AuthResponseDTO login(LoginRequestDTO request);

    User getCurrentUser(String token);
}
