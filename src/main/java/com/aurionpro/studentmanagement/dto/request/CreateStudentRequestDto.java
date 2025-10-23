package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStudentRequestDto {

	@NotBlank(message = "Student ID cannot be blank")
	private String studentId;

	@NotBlank(message = "First name is required")
	private String firstName;

	@NotBlank(message = "Last name is required")
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Email format is invalid")
	private String email;

	@NotBlank(message = "Department is required")
	private String department;

	@NotBlank(message = "Year is required")
	private String year;
}