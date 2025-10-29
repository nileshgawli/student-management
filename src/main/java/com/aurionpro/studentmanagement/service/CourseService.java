package com.aurionpro.studentmanagement.service;

import com.aurionpro.studentmanagement.dto.response.CourseDto;

import java.util.List;

/**
 * Service interface for managing course-related operations.
 * This defines the contract for business logic concerning courses.
 */
public interface CourseService {

    /**
     * Retrieves a list of all courses, with an option to filter by department.
     *
     * @param departmentId An optional ID of a department to filter the courses.
     *                     If null, all courses are returned.
     * @return A list of {@link CourseDto} objects.
     */
    List<CourseDto> getAllCourses(Long departmentId);
}