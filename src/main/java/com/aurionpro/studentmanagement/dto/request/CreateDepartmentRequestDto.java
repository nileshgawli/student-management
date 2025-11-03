package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDepartmentRequestDto {
    @NotBlank(message = "Department name is required")
    private String name;

    @Valid
    private List<CourseNestedDto> courses;
}