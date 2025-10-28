package com.aurionpro.studentmanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class StudentResponseDto {
    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private DepartmentDto department;
    private Set<CourseDto> courses;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}