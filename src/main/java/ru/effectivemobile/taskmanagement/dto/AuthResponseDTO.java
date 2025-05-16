package ru.effectivemobile.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.effectivemobile.taskmanagement.model.User;

/**
 * Data Transfer Object (DTO) for authentication response.
 * <p>
 * This DTO is returned after a successful user login. It contains the JWT access token
 * that is used for authorizing subsequent requests to the system.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@Getter
public class AuthResponseDTO {

    /**
     * JWT token issued after successful user authentication.
     * <p>
     * This token is required for authorizing future requests to protected API endpoints.
     * The token contains user authentication data and is typically included in the
     * Authorization header as a Bearer token.
     * </p>
     * Example:
     * <pre>
     * "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * </pre>
     */
    @Schema(
            description = "JWT token issued after successful authentication",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;

    @Schema(
            description = "Authenticated user information"
    )
    private User user;
}

