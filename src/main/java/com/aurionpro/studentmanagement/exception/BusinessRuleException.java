package com.aurionpro.studentmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception thrown when an operation violates a specific business rule.
 * This is a generic exception for logical errors in the application that are not
 * covered by more specific exceptions like validation or resource conflicts.
 * <p>
 * This exception results in an HTTP 400 (Bad Request) response.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BusinessRuleException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new BusinessRuleException with the specified detail message.
     *
     * @param message the detail message.
     */
	public BusinessRuleException(String message) {
		super(message);
	}
}