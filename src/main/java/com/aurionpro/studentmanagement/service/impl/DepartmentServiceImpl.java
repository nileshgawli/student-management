package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.request.CreateDepartmentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateDepartmentRequestDto;
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
 * Implementation of the {@link DepartmentService} interface.
 * Handles the business logic for operations related to academic departments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDto> getAllDepartments(String filter, Boolean isActive, Pageable pageable) {
        log.info("Fetching departments page number: {}, page size: {}, filter: '{}', isActive: {}",
                pageable.getPageNumber(), pageable.getPageSize(), filter, isActive);

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

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllActiveDepartments() {
        log.info("Fetching all active departments from the database.");
        return departmentRepository.findByIsActive(true)
                .stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DepartmentDto addDepartment(CreateDepartmentRequestDto requestDto) {
        log.info("Attempting to add new department: {}", requestDto.getName());
        if (departmentRepository.existsByName(requestDto.getName())) {
            throw new DuplicateResourceException("Department with name '" + requestDto.getName() + "' already exists.");
        }

        Department department = new Department();
        department.setName(requestDto.getName());
        Department savedDepartment = departmentRepository.save(department);
        log.info("Saved new department with ID: {}", savedDepartment.getId());

        if (requestDto.getCourses() != null && !requestDto.getCourses().isEmpty()) {
            List<Course> courses = requestDto.getCourses().stream().map(courseDto -> {
                Course course = new Course();
                course.setName(courseDto.getName());
                course.setDescription(courseDto.getDescription());
                course.setDepartment(savedDepartment);
                return course;
            }).collect(Collectors.toList());
            courseRepository.saveAll(courses);
            log.info("Saved {} courses for department {}", courses.size(), savedDepartment.getName());
        }

        return departmentMapper.toDto(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long departmentId, UpdateDepartmentRequestDto requestDto) {
        log.info("Attempting to update department ID: {}", departmentId);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));

        // Check if name is being changed and if the new name is already taken
        if (!department.getName().equalsIgnoreCase(requestDto.getName()) && departmentRepository.existsByName(requestDto.getName())) {
            throw new DuplicateResourceException("Department name '" + requestDto.getName() + "' is already in use.");
        }

        department.setName(requestDto.getName());
        Department updatedDepartment = departmentRepository.save(department);
        log.info("Department {} updated successfully.", departmentId);
        return departmentMapper.toDto(updatedDepartment);
    }

    @Override
    @Transactional
    public DepartmentDto toggleDepartmentStatus(Long departmentId) {
        log.info("Attempting to toggle status for department ID: {}", departmentId);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));

        boolean newStatus = !department.isActive();
        department.setActive(newStatus);
        log.info("Setting department {} to status: {}", departmentId, newStatus);

        // Cascade the status change to all associated courses
        List<Course> courses = courseRepository.findByDepartmentId(departmentId);
        if (!courses.isEmpty()) {
            courses.forEach(course -> course.setActive(newStatus));
            courseRepository.saveAll(courses);
            log.info("Cascaded status change to {} courses for department {}", courses.size(), department.getName());
        }

        Department updatedDepartment = departmentRepository.save(department);
        return departmentMapper.toDto(updatedDepartment);
    }
}