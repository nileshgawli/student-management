package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStudentRequestDto {
    @NotEmpty(message = "Student ID cannot be empty")
    private String studentId;

    @NotEmpty(message = "First name is required")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotEmpty(message = "Department is required")
    private String department;

    @NotEmpty(message = "Year is required")
    private String year;
}