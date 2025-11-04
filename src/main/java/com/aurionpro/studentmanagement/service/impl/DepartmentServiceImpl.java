package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.request.CreateDepartmentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateCourseNestedDto;
import com.aurionpro.studentmanagement.dto.request.UpdateDepartmentRequestDto;
import com.aurionpro.studentmanagement.dto.response.DepartmentDetailDto;
import com.aurionpro.studentmanagement.dto.response.DepartmentDto;
import com.aurionpro.studentmanagement.entity.Course;
import com.aurionpro.studentmanagement.entity.Department;
import com.aurionpro.studentmanagement.exception.DuplicateResourceException;
import com.aurionpro.studentmanagement.exception.ResourceNotFoundException;
import com.aurionpro.studentmanagement.mapper.DepartmentMapper;
import com.aurionpro.studentmanagement.repository.CourseRepository;
import com.aurionpro.studentmanagement.repository.DepartmentRepository;
import com.aurionpro.studentmanagement.service.DepartmentService;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link DepartmentService}.
 * Contains the core business logic for managing departments, including CRUD operations,
 * status management, and synchronization of associated courses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final DepartmentMapper departmentMapper;

    /**
     * {@inheritDoc}
     * This implementation uses a JPA Specification to dynamically build a query
     * that supports optional filtering by name and active status.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDto> getAllDepartments(String filter, Boolean isActive, Pageable pageable) {
        Specification<Department> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (isActive != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
            }
            if (StringUtils.hasText(filter)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filter.toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return departmentRepository.findAll(spec, pageable).map(departmentMapper::toDto);
    }

    /**
     * {@inheritDoc}
     * This is optimized for UI dropdowns where only active departments should be listed.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllActiveDepartments() {
        return departmentRepository.findByIsActive(true).stream().map(departmentMapper::toDto).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * It fetches the department and explicitly triggers the loading of the lazy-initialized
     * course list within the transaction to prevent LazyInitializationException.
     */
    @Override
    @Transactional(readOnly = true)
    public DepartmentDetailDto getDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));
        department.getCourses().size(); // Eagerly load the courses by accessing the collection size
        return departmentMapper.toDetailDto(department);
    }

    /**
     * {@inheritDoc}
     * This method creates a new department and its associated courses in a single transaction.
     * The relationship between the new courses and the department is established before saving,
     * allowing JPA's cascade functionality to persist them together.
     */
    @Override
    @Transactional
    public DepartmentDto addDepartment(CreateDepartmentRequestDto requestDto) {
        if (departmentRepository.existsByName(requestDto.getName())) {
            throw new DuplicateResourceException("Department with name '" + requestDto.getName() + "' already exists.");
        }
        Department department = new Department();
        department.setName(requestDto.getName());
        
        if (requestDto.getCourses() != null) {
            requestDto.getCourses().forEach(courseDto -> {
                Course course = new Course();
                course.setName(courseDto.getName());
                course.setDescription(courseDto.getDescription());
                course.setDepartment(department); // Set the back-reference for the relationship
                department.getCourses().add(course);
            });
        }
        return departmentMapper.toDto(departmentRepository.save(department));
    }

    /**
     * {@inheritDoc}
     * This transactional method updates the department's name and then calls a helper method
     * to synchronize the state of its associated courses with the list provided in the request.
     */
    @Override
    @Transactional
    public DepartmentDetailDto updateDepartment(Long departmentId, UpdateDepartmentRequestDto requestDto) {
        log.info("Updating department ID: {}", departmentId);
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));

        if (!department.getName().equalsIgnoreCase(requestDto.getName()) && departmentRepository.existsByName(requestDto.getName())) {
            throw new DuplicateResourceException("Department name '" + requestDto.getName() + "' is already in use.");
        }
        department.setName(requestDto.getName());

        syncCourses(department, requestDto.getCourses());

        // The save is technically redundant due to dirty checking within a @Transactional context,
        // but it can make the intent clearer.
        Department updatedDepartment = departmentRepository.save(department);
        return departmentMapper.toDetailDto(updatedDepartment);
    }

    /**
     * Synchronizes the courses of a department based on a list of DTOs.
     * This method handles three scenarios:
     * 1.  **Updating Existing Courses:** If a DTO has an ID, the corresponding course is updated.
     * 2.  **Adding New Courses:** If a DTO has no ID, a new course is created and added to the department.
     * 3.  **Removing Orphaned Courses:** Any course existing in the database but not present in the request list is deleted.
     * This is achieved by modifying the department's managed list of courses; JPA's `orphanRemoval=true` handles the deletion.
     *
     * @param department The managed Department entity to be updated.
     * @param courseDtos The list of course DTOs representing the desired state.
     */
    private void syncCourses(Department department, List<UpdateCourseNestedDto> courseDtos) {
        List<Course> managedCourses = department.getCourses();
        
        // Create a map of existing courses for efficient lookup.
        Map<Long, Course> existingCourseMap = managedCourses.stream()
            .collect(Collectors.toMap(Course::getId, Function.identity()));

        List<Long> requestIds = new ArrayList<>();

        for (UpdateCourseNestedDto dto : courseDtos) {
            if (dto.getId() != null) {
                // This DTO refers to an existing course.
                requestIds.add(dto.getId());
                Course existingCourse = existingCourseMap.get(dto.getId());
                if (existingCourse != null) { // Update its properties
                    existingCourse.setName(dto.getName());
                    existingCourse.setDescription(dto.getDescription());
                }
            } else { // This DTO represents a new course.
                Course newCourse = new Course();
                newCourse.setName(dto.getName());
                newCourse.setDescription(dto.getDescription());
                newCourse.setDepartment(department); // Set the back-reference
                managedCourses.add(newCourse); // Add the new course to the department's managed list
            }
        }
        
        // Identify courses to be removed (those in the DB but not in the update request).
        List<Course> toDelete = existingCourseMap.values().stream()
            .filter(course -> !requestIds.contains(course.getId()))
            .collect(Collectors.toList());

        // Remove the orphaned courses from the managed list.
        // Due to `orphanRemoval=true` on the relationship, JPA will delete these from the database.
        if (!toDelete.isEmpty()) {
            managedCourses.removeAll(toDelete);
            log.info("Removed {} orphan courses for department ID {}", toDelete.size(), department.getId());
        }
    }

    /**
     * {@inheritDoc}
     * This business rule dictates that changing a department's status
     * must also apply the same status to all of its associated courses.
     */
    @Override
    @Transactional
    public DepartmentDto toggleDepartmentStatus(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));

        boolean newStatus = !department.isActive();
        department.setActive(newStatus);
        log.info("Setting department {} to status: {}", departmentId, newStatus);

        // Cascade the status change to all associated courses.
        List<Course> courses = courseRepository.findByDepartmentId(departmentId);
        if (!courses.isEmpty()) {
            courses.forEach(course -> course.setActive(newStatus));
            courseRepository.saveAll(courses);
        }
        return departmentMapper.toDto(departmentRepository.save(department));
    }
}