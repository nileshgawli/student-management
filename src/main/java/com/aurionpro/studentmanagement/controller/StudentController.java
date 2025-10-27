package com.aurionpro.studentmanagement.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.studentmanagement.dto.ApiResponse;
import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Student Controller", description = "APIs for Student Management")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @Operation(summary = "Get a paginated list of students", description = "Returns a list of students with filtering, pagination, and sorting.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentResponseDto>>> getAllStudents(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Boolean isActive,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDir
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));

        Page<StudentResponseDto> studentsPage = studentService.getAllStudents(filter, isActive, pageable);
        ApiResponse<Page<StudentResponseDto>> response = new ApiResponse<>("success", "Students fetched successfully", studentsPage);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Add a new student", description = "Creates a new student record.")
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDto>> addStudent(@Valid @RequestBody CreateStudentRequestDto requestDto) {
        StudentResponseDto newStudent = studentService.addStudent(requestDto);
        ApiResponse<StudentResponseDto> response = new ApiResponse<>("success", "Student added successfully", newStudent);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing student", description = "Updates a student's details by their business ID.")
    @PutMapping("/{studentId}")
    public ResponseEntity<ApiResponse<StudentResponseDto>> updateStudent(
            @PathVariable String studentId,
            @Valid @RequestBody UpdateStudentRequestDto requestDto) {
        StudentResponseDto updatedStudent = studentService.updateStudent(studentId, requestDto);
        ApiResponse<StudentResponseDto> response = new ApiResponse<>("success", "Student updated successfully", updatedStudent);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Soft delete a student", description = "Marks a student as inactive by their business ID.")
    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteStudent(@PathVariable String studentId) {
        studentService.softDeleteStudent(studentId);
        ApiResponse<Void> response = new ApiResponse<>("success", "Student marked as inactive successfully", null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Toggle student status", description = "Activates or deactivates a student by their business ID.")
    @PatchMapping("/{studentId}/toggle-status")
    public ResponseEntity<ApiResponse<StudentResponseDto>> toggleStudentStatus(@PathVariable String studentId) {
        StudentResponseDto updatedStudent = studentService.toggleStudentStatus(studentId);
        ApiResponse<StudentResponseDto> response = new ApiResponse<>("success", "Student status updated successfully", updatedStudent);
        return ResponseEntity.ok(response);
    }
}