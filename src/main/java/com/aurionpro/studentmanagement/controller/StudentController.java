package com.aurionpro.studentmanagement.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aurionpro.studentmanagement.dto.ApiResponse;
import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get all active students with pagination", description = "Returns a paginated list of all students whose status is 'active'.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentResponseDto>>> getAllActiveStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentResponseDto> studentsPage = studentService.getAllActiveStudents(page, size);
        ApiResponse<Page<StudentResponseDto>> response = new ApiResponse<>("success", "Students fetched successfully", studentsPage);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add a new student", description = "Creates a new student record in the system.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Student created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input, validation failed", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Student with this ID already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDto>> addStudent(@Valid @RequestBody CreateStudentRequestDto requestDto) {
        StudentResponseDto newStudent = studentService.addStudent(requestDto);
        ApiResponse<StudentResponseDto> response = new ApiResponse<>("success", "Student added successfully", newStudent);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing student", description = "Updates the details of an existing student by their ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input, validation failed", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDto>> updateStudent(
            @PathVariable("id") String studentId,
            @Valid @RequestBody UpdateStudentRequestDto requestDto) {
        StudentResponseDto updatedStudent = studentService.updateStudent(studentId, requestDto);
        ApiResponse<StudentResponseDto> response = new ApiResponse<>("success", "Student updated successfully", updatedStudent);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Soft delete a student", description = "Marks a student as inactive in the system by their ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> softDeleteStudent(@PathVariable("id") String studentId) {
        studentService.softDeleteStudent(studentId);
        ApiResponse<Void> response = new ApiResponse<>("success", "Student marked as inactive successfully", null);
        return ResponseEntity.ok(response);
    }
}