package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateStudentRequestDto {

    @NotBlank(message = "Student ID cannot be blank")
    @Size(max = 100, message = "Student ID must be less than 100 characters")
    private String studentId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private List<Long> courseIds;
}