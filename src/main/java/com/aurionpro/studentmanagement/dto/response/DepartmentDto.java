package com.aurionpro.studentmanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing an academic department.
 * Used in API responses to provide department information.
 */
@Getter
@Setter
public class DepartmentDto {
    /**
     * The unique database identifier of the department.
     */
    private Long id;

    /**
     * The name of the department.
     */
    private String name;
}