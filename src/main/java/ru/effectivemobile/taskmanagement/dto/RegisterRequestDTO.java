package ru.effectivemobile.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for user registration.
 * <p>
 * This DTO is used to capture the necessary information required to register a new user.
 * It includes the user's email and password, both of which are validated before creating an account.
 * </p>
 */
@Data
@AllArgsConstructor
@Getter
public class RegisterRequestDTO {


    /**
     * User's email address that will be used for registration.
     * <p>
     * The email must be non-blank and in a valid format.
     * Example: "newuser@example.com"
     * </p>
     */
    @Schema(description = "User's full name", example = "John Doe")
    @NotBlank(message = "User full name be empty")
    private String fullName;

    /**
     * User's email address that will be used for registration.
     * <p>
     * The email must be non-blank and in a valid format.
     * Example: "newuser@example.com"
     * </p>
     */
    @Schema(description = "User's email address", example = "newuser@example.com")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Password for the new account, with a minimum length of 6 characters.
     * <p>
     * The password must be non-blank and at least 6 characters long to ensure sufficient security.
     * Example: "securePassword1"
     * </p>
     */
    @Schema(description = "Password for the new account (minimum 6 characters)", example = "securePassword1")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
