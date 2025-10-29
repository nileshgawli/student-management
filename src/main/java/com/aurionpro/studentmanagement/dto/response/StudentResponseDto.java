package com.aurionpro.studentmanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

/**
 * Data Transfer Object for sending detailed student information to the client.
 * This comprehensive DTO includes nested objects for the student's department
 * and enrolled courses, as well as auditing timestamps.
 */
@Getter
@Setter
public class StudentResponseDto {

    /**
     * The internal database ID of the student.
     */
    private Long id;

    /**
     * The unique business ID of the student.
     */
    private String studentId;

    /**
     * The first name of the student.
     */
    private String firstName;

    /**
     * The last name of the student.
     */
    private String lastName;

    /**
     * The email address of the student.
     */
    private String email;

    /**
     * The department the student is assigned to.
     */
    private DepartmentDto department;

    /**
     * A set of courses the student is enrolled in.
     */
    private Set<CourseDto> courses;

    /**
     * The current status of the student record (true for active, false for inactive).
     */
    private boolean isActive;

    /**
     * The UTC timestamp when the student record was first created.
     */
    private Instant createdAt;

    /**
     * The UTC timestamp of the last update to the student record.
     */
    private Instant updatedAt;
}