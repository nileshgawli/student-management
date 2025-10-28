package com.aurionpro.studentmanagement.controller;

import com.aurionpro.studentmanagement.dto.ApiResponse;
import com.aurionpro.studentmanagement.dto.response.DepartmentDto;
import com.aurionpro.studentmanagement.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Department Controller", description = "APIs for Department Management")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "Get all departments", description = "Returns a list of all available departments.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        ApiResponse<List<DepartmentDto>> response = new ApiResponse<>("success", "Departments fetched successfully", departments);
        return ResponseEntity.ok(response);
    }
}