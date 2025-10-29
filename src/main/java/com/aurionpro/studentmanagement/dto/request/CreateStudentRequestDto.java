package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object for creating a new student record.
 * This class encapsulates all the necessary information required from the client
 * to register a new student and includes validation annotations to ensure data integrity.
 */
@Getter
@Setter
public class CreateStudentRequestDto {

    /**
     * The unique business identifier for the student.
     * It must not be blank and has a maximum length of 100 characters.
     */
    @NotBlank(message = "Student ID cannot be blank")
    @Size(max = 100, message = "Student ID must be less than 100 characters")
    private String studentId;

    /**
     * The student's first name. This field is mandatory.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * The student's last name. This field is mandatory.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * The student's email address. It must be a valid email format and is mandatory.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    /**
     * The database ID of the department the student will be assigned to.
     * This field is mandatory.
     */
    @NotNull(message = "Department ID is required")
    private Long departmentId;

    /**
     * An optional list of database IDs for the courses the student will be enrolled in.
     */
    private List<Long> courseIds;
}