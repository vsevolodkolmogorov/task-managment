package ru.effectivemobile.taskmanagement.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for managing JSON Web Tokens (JWT).
 * <p>
 * This service provides methods for generating JWT tokens, extracting information from
 * them (such as the username), and validating the tokens against user details.
 * </p>
 */
public interface JwtService {

    /**
     * Generates a JWT token based on the provided user details.
     * <p>
     * This method creates a JWT token, which can be used for authenticating requests
     * and authorizing access to protected endpoints. The token includes the username
     * and possibly other user-related information.
     * </p>
     *
     * @param userDetails the user details to base the token on
     * @return a generated JWT token as a string
     */
    String generateToken(UserDetails userDetails);

    /**
     * Extracts the username from the given JWT token.
     * <p>
     * This method is used to decode the token and retrieve the username that is embedded
     * within it. The username is typically used to identify the user making the request.
     * </p>
     *
     * @param token the JWT token to extract the username from
     * @return the username embedded in the token
     */
    String extractUsername(String token);

    /**
     * Validates the given JWT token against the provided user details.
     * <p>
     * This method checks if the token is valid (not expired, signed correctly, etc.)
     * and if it matches the given user details (such as username).
     * </p>
     *
     * @param token the JWT token to be validated
     * @param userDetails the user details to match against
     * @return true if the token is valid and matches the user details, false otherwise
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}
