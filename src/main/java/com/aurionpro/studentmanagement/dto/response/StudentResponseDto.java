package com.aurionpro.studentmanagement.dto.response;

import com.aurionpro.studentmanagement.entity.Department;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class StudentResponseDto {
    private Long id; 
    private String studentId; 
    private String firstName;
    private String lastName;
    private String email;
    private Department department;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}