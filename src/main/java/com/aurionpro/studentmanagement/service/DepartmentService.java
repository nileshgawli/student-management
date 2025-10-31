package com.aurionpro.studentmanagement.service;

import com.aurionpro.studentmanagement.dto.request.CreateDepartmentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateDepartmentRequestDto;
import com.aurionpro.studentmanagement.dto.response.DepartmentDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for department-related business operations.
 * It outlines the methods available for managing academic departments.
 */
public interface DepartmentService {

    /**
     * Retrieves a paginated and filtered list of departments.
     * @param filter   Text to search by department name.
     * @param isActive Status to filter by.
     * @param pageable Pagination and sorting information.
     * @return A Page of DepartmentDto objects.
     */
    Page<DepartmentDto> getAllDepartments(String filter, Boolean isActive, Pageable pageable);

    /**
     * Retrieves a list of all active departments, suitable for dropdowns.
     * @return A list of active DepartmentDto objects.
     */
    List<DepartmentDto> getAllActiveDepartments();

    /**
     * Creates a new department and its associated courses.
     * @param requestDto DTO containing department and course information.
     * @return The newly created DepartmentDto.
     */
    DepartmentDto addDepartment(CreateDepartmentRequestDto requestDto);

    /**
     * Updates an existing department's name.
     * @param departmentId The ID of the department to update.
     * @param requestDto   DTO containing the new name.
     * @return The updated DepartmentDto.
     */
    DepartmentDto updateDepartment(Long departmentId, UpdateDepartmentRequestDto requestDto);

    /**
     * Toggles the active status of a department and all its associated courses.
     * @param departmentId The ID of the department to toggle.
     * @return The updated DepartmentDto with the new status.
     */
    DepartmentDto toggleDepartmentStatus(Long departmentId);
}