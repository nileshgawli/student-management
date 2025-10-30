package com.aurionpro.studentmanagement.controller;

import com.aurionpro.studentmanagement.dto.ApiResponse;
import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

/**
 * REST controller for handling all student-related API requests.
 * This class exposes endpoints for CRUD operations and data exports for students.
 */
@RestController
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Student Controller", description = "APIs for Student Management")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Retrieves a paginated list of students with optional filtering and sorting.
     *
     * @param filter   Optional search term to filter students by ID, name, or email.
     * @param isActive Optional status to filter students by (true for active, false for inactive).
     * @param page     The page number to retrieve (0-indexed).
     * @param size     The number of students per page.
     * @param sortBy   The field to sort the results by.
     * @param sortDir  The direction of the sort (ASC or DESC).
     * @return A {@link ResponseEntity} containing a paginated list of students.
     */
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

    /**
     * Creates a new student record.
     *
     * @param requestDto The DTO containing the data for the new student.
     * @return A {@link ResponseEntity} with the created student's data and a 201 status.
     */
    @Operation(summary = "Add a new student", description = "Creates a new student record.")
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDto>> addStudent(@Valid @RequestBody CreateStudentRequestDto requestDto) {
        StudentResponseDto newStudent = studentService.addStudent(requestDto);
        ApiResponse<StudentResponseDto> response = new ApiResponse<>("success", "Student added successfully", newStudent);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Updates an existing student's details.
     *
     * @param studentId  The business ID of the student to update.
     * @param requestDto The DTO containing the updated student data.
     * @return A {@link ResponseEntity} with the updated student's data.
     */
    @Operation(summary = "Update an existing student", description = "Updates a student's details by their business ID.")
    @PutMapping("/{studentId}")
    public ResponseEntity<ApiResponse<StudentResponseDto>> updateStudent(
            @PathVariable String studentId,
            @Valid @RequestBody UpdateStudentRequestDto requestDto) {
        StudentResponseDto updatedStudent = studentService.updateStudent(studentId, requestDto);
        ApiResponse<StudentResponseDto> response = new ApiResponse<>("success", "Student updated successfully", updatedStudent);
        return ResponseEntity.ok(response);
    }

    /**
     * Marks a student as inactive (soft delete).
     *
     * @param studentId The business ID of the student to deactivate.
     * @return A {@link ResponseEntity} confirming the operation.
     */
    @Operation(summary = "Soft delete a student", description = "Marks a student as inactive by their business ID.")
    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteStudent(@PathVariable String studentId) {
        studentService.softDeleteStudent(studentId);
        ApiResponse<Void> response = new ApiResponse<>("success", "Student marked as inactive successfully", null);
        return ResponseEntity.ok(response);
    }

    /**
     * Toggles the active status of a student (active to inactive or vice-versa).
     *
     * @param studentId The business ID of the student whose status is to be toggled.
     * @return A {@link ResponseEntity} with the student's updated data.
     */
    @Operation(summary = "Toggle student status", description = "Activates or deactivates a student by their business ID.")
    @PatchMapping("/{studentId}/toggle-status")
    public ResponseEntity<ApiResponse<StudentResponseDto>> toggleStudentStatus(@PathVariable String studentId) {
        StudentResponseDto updatedStudent = studentService.toggleStudentStatus(studentId);
        ApiResponse<StudentResponseDto> response = new ApiResponse<>("success", "Student status updated successfully", updatedStudent);
        return ResponseEntity.ok(response);
    }

    /**
     * Exports a list of students to an Excel (XLSX) file based on optional filters.
     * This method writes the file directly to the {@link HttpServletResponse}.
     *
     * @param filter   Optional search term to filter the exported students.
     * @param isActive Optional status to filter the exported students.
     * @param response The HttpServletResponse to which the Excel file will be written.
     * @throws IOException if an I/O error occurs during file writing.
     */
    @Operation(summary = "Download students as an Excel file", description = "Generates and downloads an XLSX file containing students based on the provided filters.")
    @GetMapping("/download/xlsx")
    public void downloadStudentsAsExcel(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Boolean isActive,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=students_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        studentService.generateStudentsExcel(filter, isActive, response);
    }

    /**
     * Exports a list of students to a CSV file based on optional filters.
     * This method writes the file directly to the {@link HttpServletResponse}.
     *
     * @param filter   Optional search term to filter the exported students.
     * @param isActive Optional status to filter the exported students.
     * @param response The HttpServletResponse to which the CSV file will be written.
     * @throws IOException if an I/O error occurs during file writing.
     */
    @Operation(summary = "Download students as a CSV file", description = "Generates and downloads a CSV file containing students based on the provided filters.")
    @GetMapping("/download/csv")
    public void downloadStudentsAsCsv(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Boolean isActive,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("text/csv");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=students_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);

        studentService.generateStudentsCsv(filter, isActive, response);
    }
}