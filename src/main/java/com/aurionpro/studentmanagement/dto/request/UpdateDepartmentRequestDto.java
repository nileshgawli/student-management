package com.aurionpro.studentmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDepartmentRequestDto {
    @NotBlank(message = "Department name is required")
    private String name;
}