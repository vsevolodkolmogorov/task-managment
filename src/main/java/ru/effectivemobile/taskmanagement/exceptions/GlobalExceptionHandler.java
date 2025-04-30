package ru.effectivemobile.taskmanagement.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler to catch and process exceptions thrown in the application.
 * <p>
 * This class handles various types of exceptions such as validation errors, authentication failures,
 * access issues, and custom exceptions (e.g., user not found, email already registered).
 * It then returns appropriate HTTP status codes and messages in the response.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the exception when a user tries to register with an already registered email.
     * <p>
     * This exception is thrown when an attempt is made to register a new user with an email
     * that already exists in the system.
     * </p>
     *
     * @param ex      The exception that was thrown
     * @param request The HTTP request object, used to retrieve the request URI
     * @return A ResponseEntity containing the error details and HTTP status code 409 (Conflict)
     */
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyRegisteredException ex,
                                                           HttpServletRequest request) {
        return createResponse(ex.getMessage(), request, HttpStatus.CONFLICT);
    }

    /**
     * Handles the exception when a user is not found in the system.
     * <p>
     * This exception is thrown when the system attempts to fetch user details but fails
     * because the user does not exist in the database.
     * </p>
     *
     * @param ex      The exception that was thrown
     * @param request The HTTP request object, used to retrieve the request URI
     * @return A ResponseEntity containing the error details and HTTP status code 404 (Not Found)
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(UserNotFoundException ex,
                                                           HttpServletRequest request) {
        return createResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles the exception for invalid login credentials.
     * <p>
     * This exception is thrown when the user provides incorrect login credentials (e.g., invalid username or password).
     * </p>
     *
     * @param ex      The exception that was thrown
     * @param request The HTTP request object, used to retrieve the request URI
     * @return A ResponseEntity containing the error details and HTTP status code 401 (Unauthorized)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex,
                                                              HttpServletRequest request) {
        return createResponse("Invalid username or password", request, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles the exception when the user does not have permission to access a resource.
     * <p>
     * This exception is thrown when a user attempts to access a resource they are not authorized to access.
     * </p>
     *
     * @param ex      The exception that was thrown
     * @param request The HTTP request object, used to retrieve the request URI
     * @return A ResponseEntity containing the error details and HTTP status code 403 (Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest request) {
        return createResponse("Access denied", request, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles validation exceptions triggered when request data does not meet validation constraints.
     * <p>
     * This exception is thrown when there are validation errors in the request body, such as missing or invalid fields.
     * </p>
     *
     * @param ex      The exception that was thrown
     * @param request The HTTP request object, used to retrieve the request URI
     * @return A ResponseEntity containing the error details and HTTP status code 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                   HttpServletRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1 + "; " + msg2
                ));

        String errorMessage = fieldErrors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(" | "));

        return createResponse(errorMessage, request, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a standardized error response that contains information about the exception.
     * <p>
     * This method is used by various exception handlers to create a consistent response format for error messages.
     * </p>
     *
     * @param message The error message to be included in the response
     * @param request The HTTP request object, used to retrieve the request URI
     * @param httpStatus The HTTP status code that should be returned
     * @return A ResponseEntity containing the error response object and HTTP status
     */
    private ResponseEntity<ErrorResponse> createResponse(String message,
                                                         HttpServletRequest request, HttpStatus httpStatus) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDate.now())
                .status(httpStatus.value())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(httpStatus)
                .body(error);
    }
}
