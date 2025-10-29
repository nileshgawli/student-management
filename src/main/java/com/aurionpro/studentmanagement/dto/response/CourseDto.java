package com.aurionpro.studentmanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing a course.
 * This is used in API responses to provide course details to the client.
 */
@Getter
@Setter
public class CourseDto {

    /**
     * The unique database identifier of the course.
     */
    private Long id;

    /**
     * The name of the course.
     */
    private String name;

    /**
     * A brief description of the course.
     */
    private String description;
}