package com.aurionpro.studentmanagement.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * A Data Transfer Object for sending detailed information about a single department to the client.
 * In addition to basic details, it includes the department's active status and a complete
 * list of all courses associated with it.
 */
@Getter
@Setter
public class DepartmentDetailDto {

    /**
     * The unique database identifier of the department.
     */
    private Long id;

    /**
     * The name of the department.
     */
    private String name;

    /**
     * A flag indicating whether the department's record is active.
     * An inactive department is typically hidden from selection lists in the UI.
     */
    private boolean isActive;

    /**
     * A list of {@link CourseDto} objects representing all courses
     * offered by this department.
     */
    private List<CourseDto> courses;
}