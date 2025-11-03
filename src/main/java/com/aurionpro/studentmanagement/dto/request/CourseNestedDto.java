package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseNestedDto {
	@NotBlank(message = "Course name cannot be blank")
	private String name;
	private String description;
}