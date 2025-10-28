package com.aurionpro.studentmanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.List;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final List<String> errors;

	public ValidationException(String message, List<String> errors) {
		super(message);
		this.errors = errors;
	}
}