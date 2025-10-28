package com.aurionpro.studentmanagement.controller;

import com.aurionpro.studentmanagement.dto.ApiResponse;
import com.aurionpro.studentmanagement.dto.response.CourseDto;
import com.aurionpro.studentmanagement.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Course Controller", description = "APIs for Course Management")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Get all courses", description = "Returns a list of all courses, optionally filtered by department.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDto>>> getAllCourses(
            @RequestParam(required = false) Long departmentId
    ) {
        List<CourseDto> courses = courseService.getAllCourses(departmentId);
        ApiResponse<List<CourseDto>> response = new ApiResponse<>("success", "Courses fetched successfully", courses);
        return ResponseEntity.ok(response);
    }
}