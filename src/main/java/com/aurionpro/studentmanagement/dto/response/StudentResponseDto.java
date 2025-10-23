package com.aurionpro.studentmanagement.dto.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentResponseDto {
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String year;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}