package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for updating an existing department.
 * This DTO allows for changing the department's name and also synchronizing its
 * entire list of associated courses. The list of courses can include existing
 * courses to be updated and new courses to be created.
 */
@Getter
@Setter
public class UpdateDepartmentRequestDto {

    /**
     * The updated name for the department. This field is mandatory.
     */
    @NotBlank(message = "Department name is required")
    private String name;

    /**
     * A list of courses to be associated with this department.
     * The {@code @Valid} annotation ensures that each {@link UpdateCourseNestedDto}
     * in the list is also validated against its own constraints.
     */
    @Valid
    private List<UpdateCourseNestedDto> courses;
}