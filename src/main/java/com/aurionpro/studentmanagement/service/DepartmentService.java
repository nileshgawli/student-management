package com.aurionpro.studentmanagement.service;

import com.aurionpro.studentmanagement.dto.response.DepartmentDto;

import java.util.List;

/**
 * Service interface for department-related business operations.
 * It outlines the methods available for managing academic departments.
 */
public interface DepartmentService {

    /**
     * Retrieves a list of all available academic departments.
     *
     * @return A list of {@link DepartmentDto} objects, each representing a department.
     */
    List<DepartmentDto> getAllDepartments();
}