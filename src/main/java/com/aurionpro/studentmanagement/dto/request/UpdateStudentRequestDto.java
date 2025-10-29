package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object for updating an existing student's information.
 * This class contains the fields that are permissible to change for a student.
 * The student's business ID (studentId) is not included as it is immutable.
 */
@Getter
@Setter
public class UpdateStudentRequestDto {

    /**
     * The student's updated first name. This field is mandatory.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * The student's updated last name. This field is mandatory.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * The student's updated email address. It must be a valid email format and is mandatory.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    /**
     * The updated database ID of the student's assigned department. This field is mandatory.
     */
    @NotNull(message = "Department ID is required")
    private Long departmentId;

    /**
     * An optional list of updated database IDs for the student's course enrollments.
     */
    private List<Long> courseIds;
}