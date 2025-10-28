package com.aurionpro.studentmanagement.service;

import com.aurionpro.studentmanagement.dto.response.DepartmentDto;

import java.util.List;

public interface DepartmentService {
    List<DepartmentDto> getAllDepartments();
}