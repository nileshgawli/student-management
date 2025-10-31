package com.aurionpro.studentmanagement.controller;

import com.aurionpro.studentmanagement.dto.ApiResponse;
import com.aurionpro.studentmanagement.dto.request.CreateDepartmentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateDepartmentRequestDto;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Department Controller", description = "APIs for Department Management")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

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

    @Operation(summary = "Get all active departments", description = "Returns a list of all active departments, suitable for UI dropdowns.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllActiveDepartments() {
        List<DepartmentDto> departments = departmentService.getAllActiveDepartments();
        ApiResponse<List<DepartmentDto>> response = new ApiResponse<>("success", "Active departments fetched successfully", departments);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add a new department", description = "Creates a new department and its associated courses.")
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDto>> addDepartment(@Valid @RequestBody CreateDepartmentRequestDto requestDto) {
        DepartmentDto newDepartment = departmentService.addDepartment(requestDto);
        ApiResponse<DepartmentDto> response = new ApiResponse<>("success", "Department added successfully", newDepartment);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing department", description = "Updates a department's details by its ID.")
    @PutMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<DepartmentDto>> updateDepartment(
            @PathVariable Long departmentId,
            @Valid @RequestBody UpdateDepartmentRequestDto requestDto) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(departmentId, requestDto);
        ApiResponse<DepartmentDto> response = new ApiResponse<>("success", "Department updated successfully", updatedDepartment);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Toggle department status", description = "Activates or deactivates a department and its associated courses.")
    @PatchMapping("/{departmentId}/toggle-status")
    public ResponseEntity<ApiResponse<DepartmentDto>> toggleDepartmentStatus(@PathVariable Long departmentId) {
        DepartmentDto updatedDepartment = departmentService.toggleDepartmentStatus(departmentId);
        ApiResponse<DepartmentDto> response = new ApiResponse<>("success", "Department status updated successfully", updatedDepartment);
        return ResponseEntity.ok(response);
    }
}