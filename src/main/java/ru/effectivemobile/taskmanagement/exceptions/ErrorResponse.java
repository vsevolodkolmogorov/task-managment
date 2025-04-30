package ru.effectivemobile.taskmanagement.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A class that represents an error response returned by the API in case of an exception.
 * <p>
 * This class contains essential information about the error, including the timestamp of when the error occurred,
 * the HTTP status code, a descriptive error message, and the path where the error occurred.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * The timestamp when the error occurred.
     * <p>
     * This field stores the date (in the format {@link LocalDate}) when the error was triggered,
     * providing useful context for debugging and logging purposes.
     * </p>
     */
    private LocalDate timestamp;

    /**
     * The HTTP status code associated with the error.
     * <p>
     * This field contains the status code of the HTTP response (e.g., 404 for "Not Found", 500 for "Internal Server Error").
     * </p>
     */
    private int status;

    /**
     * A detailed message describing the error.
     * <p>
     * This field provides a human-readable message to describe what went wrong, such as validation failure or system issues.
     * </p>
     */
    private String message;

    /**
     * The path of the request that caused the error.
     * <p>
     * This field contains the URL path of the request that led to the error, helping to identify the affected resource.
     * </p>
     */
    private String path;
}
