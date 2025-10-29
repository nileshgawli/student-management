package com.aurionpro.studentmanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.List;

/**
 * A custom exception used for handling complex, service-level validation failures.
 * This is thrown when one or more validation rules are violated during a business operation,
 * and it can carry a list of specific error messages.
 * <p>
 * This exception results in an HTTP 400 (Bad Request) response.
 */
@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
     * A list of detailed error messages explaining the validation failures.
     */
	private final List<String> errors;

	/**
     * Constructs a new ValidationException with a summary message and a list of detailed errors.
     *
     * @param message A summary message for the exception.
     * @param errors  A list of specific validation error messages.
     */
	public ValidationException(String message, List<String> errors) {
		super(message);
		this.errors = errors;
	}
}