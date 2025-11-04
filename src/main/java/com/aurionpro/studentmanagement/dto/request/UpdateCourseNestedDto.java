package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * A Data Transfer Object used to represent a course within a parent DTO,
 * specifically for updating a department and its associated courses.
 * This nested structure allows for creating, updating, or associating courses
 * in a single API call.
 */
@Getter
@Setter
public class UpdateCourseNestedDto {

    /**
     * The unique identifier of the course.
     * If this ID is provided and exists, the corresponding course will be updated.
     * If the ID is null or not provided, a new course will be created and
     * associated with the department.
     */
    private Long id;

    /**
     * The name of the course. This field is mandatory and cannot be blank.
     */
    @NotBlank(message = "Course name cannot be blank")
    private String name;

    /**
     * An optional detailed description of the course.
     */
    private String description;
}