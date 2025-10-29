package com.aurionpro.studentmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception thrown when a requested resource cannot be found in the system.
 * This is typically used when a lookup by a specific identifier (e.g., student ID)
 * yields no result.
 * <p>
 * This exception results in an HTTP 404 (Not Found) response.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2064768789496941183L;

	/**
     * Constructs a new ResourceNotFoundException with a default message.
     */
	public ResourceNotFoundException() {
        super("Resource not found");
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}