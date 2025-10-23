package com.aurionpro.studentmanagement.dto;

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
}