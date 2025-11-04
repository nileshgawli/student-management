package com.aurionpro.studentmanagement.controller;

import com.aurionpro.studentmanagement.dto.ApiResponse;
import com.aurionpro.studentmanagement.dto.request.CreateDepartmentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateDepartmentRequestDto;
import com.aurionpro.studentmanagement.dto.response.DepartmentDetailDto;
import com.aurionpro.studentmanagement.dto.response.DepartmentDto;
import com.aurionpro.studentmanagement.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing all department-related API requests.
 * Exposes endpoints for creating, retrieving, updating, and managing the status of departments.
 */
@RestController
@RequestMapping("/api/v1/departments")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Department Controller", description = "APIs for Department Management")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Retrieves a paginated and filtered list of departments.
     *
     * @param filter   An optional search term to filter departments by name.
     * @param isActive An optional status to filter departments by (true for active, false for inactive).
     * @param page     The page number to retrieve (0-indexed).
     * @param size     The number of departments per page.
     * @param sortBy   The field to sort the results by (e.g., "name").
     * @param sortDir  The direction of the sort (ASC or DESC).
     * @return A {@link ResponseEntity} containing a paginated list of {@link DepartmentDto}.
     */
    @Operation(summary = "Get a paginated list of departments", description = "Returns a list of departments with filtering, pagination, and sorting.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DepartmentDto>>> getAllDepartments(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDir
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<DepartmentDto> departmentsPage = departmentService.getAllDepartments(filter, isActive, pageable);
        ApiResponse<Page<DepartmentDto>> response = new ApiResponse<>("success", "Departments fetched successfully", departmentsPage);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a simple list of all currently active departments.
     * This endpoint is optimized for populating UI elements like dropdown menus.
     *
     * @return A {@link ResponseEntity} containing a list of active {@link DepartmentDto}.
     */
    @Operation(summary = "Get all active departments", description = "Returns a list of all active departments, suitable for UI dropdowns.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllActiveDepartments() {
        List<DepartmentDto> departments = departmentService.getAllActiveDepartments();
        ApiResponse<List<DepartmentDto>> response = new ApiResponse<>("success", "Active departments fetched successfully", departments);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the details of a single department, including its full list of associated courses.
     *
     * @param departmentId The unique ID of the department to retrieve.
     * @return A {@link ResponseEntity} containing the detailed department information.
     */
    @Operation(summary = "Get a single department by ID with its courses", description = "Returns department details including its full list of courses.")
    @GetMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<DepartmentDetailDto>> getDepartmentById(@PathVariable Long departmentId) {
        DepartmentDetailDto department = departmentService.getDepartmentById(departmentId);
        ApiResponse<DepartmentDetailDto> response = new ApiResponse<>("success", "Department details fetched successfully", department);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new department and can optionally create associated courses in the same transaction.
     *
     * @param requestDto The DTO containing the data for the new department and its courses.
     * @return A {@link ResponseEntity} with the created department's data and a 201 CREATED status.
     */
    @Operation(summary = "Add a new department", description = "Creates a new department and its associated courses.")
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDto>> addDepartment(@Valid @RequestBody CreateDepartmentRequestDto requestDto) {
        DepartmentDto newDepartment = departmentService.addDepartment(requestDto);
        ApiResponse<DepartmentDto> response = new ApiResponse<>("success", "Department added successfully", newDepartment);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Updates an existing department's details and synchronizes its list of courses.
     * This can involve creating new courses, updating existing ones, and removing those no longer associated.
     *
     * @param departmentId The ID of the department to update.
     * @param requestDto   The DTO containing the updated information for the department and its courses.
     * @return A {@link ResponseEntity} with the updated, detailed department data.
     */
    @Operation(summary = "Update an existing department and its courses", description = "Updates a department's details and synchronizes its courses.")
    @PutMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<DepartmentDetailDto>> updateDepartment(
            @PathVariable Long departmentId,
            @Valid @RequestBody UpdateDepartmentRequestDto requestDto) {
        DepartmentDetailDto updatedDepartment = departmentService.updateDepartment(departmentId, requestDto);
        ApiResponse<DepartmentDetailDto> response = new ApiResponse<>("success", "Department updated successfully", updatedDepartment);
        return ResponseEntity.ok(response);
    }

    /**
     * Toggles the active status of a department.
     * Deactivating a department will also deactivate all of its associated courses.
     *
     * @param departmentId The ID of the department whose status is to be toggled.
     * @return A {@link ResponseEntity} containing the department data with the updated status.
     */
    @Operation(summary = "Toggle department status", description = "Activates or deactivates a department and its associated courses.")
    @PatchMapping("/{departmentId}/toggle-status")
    public ResponseEntity<ApiResponse<DepartmentDto>> toggleDepartmentStatus(@PathVariable Long departmentId) {
        DepartmentDto updatedDepartment = departmentService.toggleDepartmentStatus(departmentId);
        ApiResponse<DepartmentDto> response = new ApiResponse<>("success", "Department status updated successfully", updatedDepartment);
        return ResponseEntity.ok(response);
    }
}