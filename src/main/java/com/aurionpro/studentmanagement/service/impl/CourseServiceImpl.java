package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.response.CourseDto;
import com.aurionpro.studentmanagement.entity.Course;
import com.aurionpro.studentmanagement.mapper.CourseMapper;
import com.aurionpro.studentmanagement.repository.CourseRepository;
import com.aurionpro.studentmanagement.service.CourseService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Retrieves all active courses, optionally filtering them by a specific department ID.
     * This is used to populate dropdowns in the UI, ensuring only valid, active courses can be selected.
     * The operation is performed within a read-only transaction for optimized database performance.
     *
     * @param departmentId The ID of the department to filter by. If this parameter is null,
     *                     the method will return all active courses from all departments.
     * @return A list of {@link CourseDto} representing the fetched courses. The list will be
     *         empty if no courses are found that match the criteria.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses(Long departmentId) {
        if (departmentId != null) {
            log.info("Fetching active courses for departmentId: {}", departmentId);
        } else {
            log.info("Fetching all active courses.");
        }

        List<Course> courses;
        if (departmentId != null) {
            // Fetch active courses belonging to a specific department
            courses = courseRepository.findByDepartmentIdAndIsActive(departmentId, true);
        } else {
            // Fetch all active courses if no department ID is provided
            courses = courseRepository.findAll().stream().filter(Course::isActive).collect(Collectors.toList());
        }

        log.info("Found {} active courses.", courses.size());
        // Map the list of Course entities to a list of CourseDto objects
        return courses.stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }
}