package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.response.CourseDto;
import com.aurionpro.studentmanagement.entity.Course;
import com.aurionpro.studentmanagement.mapper.CourseMapper;
import com.aurionpro.studentmanagement.repository.CourseRepository;
import com.aurionpro.studentmanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link CourseService} interface.
 * This class contains the business logic for handling course data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    /**
     * Retrieves all courses, optionally filtering them by a specific department ID.
     * The operation is performed within a read-only transaction for optimized database performance.
     *
     * @param departmentId The ID of the department to filter by. If this parameter is null,
     *                     the method will return all courses from all departments.
     * @return A list of {@link CourseDto} representing the fetched courses. The list will be
     *         empty if no courses are found that match the criteria.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses(Long departmentId) {
        if (departmentId != null) {
            log.info("Fetching courses for departmentId: {}", departmentId);
        } else {
            log.info("Fetching all courses.");
        }

        List<Course> courses;
        if (departmentId != null) {
            // Fetch courses belonging to a specific department
            courses = courseRepository.findByDepartmentId(departmentId);
        } else {
            // Fetch all courses if no department ID is provided
            courses = courseRepository.findAll();
        }

        log.info("Found {} courses.", courses.size());
        // Map the list of Course entities to a list of CourseDto objects
        return courses.stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }
}