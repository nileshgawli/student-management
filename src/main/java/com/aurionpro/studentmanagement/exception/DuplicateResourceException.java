package com.aurionpro.studentmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception thrown when an attempt is made to create a resource that already exists,
 * violating a uniqueness constraint (e.g., creating a student with an already used email or student ID).
 * <p>
 * This exception results in an HTTP 409 (Conflict) response.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new DuplicateResourceException with a default message.
     */
	public DuplicateResourceException() {
		super("Resource already exists");
	}

	/**
     * Constructs a new DuplicateResourceException with the specified detail message.
     *
     * @param message the detail message.
     */
	public DuplicateResourceException(String message) {
		super(message);
	}

	/**
     * Constructs a new DuplicateResourceException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
	public DuplicateResourceException(String message, Throwable cause) {
		super(message, cause);
	}
}