package ru.effectivemobile.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for user login request.
 * <p>
 * This DTO is used to capture the necessary credentials required for authenticating a user during the login process.
 * It includes the user's email and password, both of which are validated before authentication.
 * </p>
 */
@Data
@AllArgsConstructor
@Getter
public class LoginRequestDTO {

    /**
     * User's email address used for authentication.
     * <p>
     * The email must be non-blank and in a valid format for the authentication process.
     * Example: "user@example.com"
     * </p>
     */
    @Schema(description = "User's email address", example = "user@example.com")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Password associated with the user account for authentication.
     * <p>
     * The password must be non-blank and at least 6 characters long to ensure secure login.
     * Example: "password123"
     * </p>
     */
    @Schema(description = "User's password (minimum 6 characters)", example = "password123")
    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
