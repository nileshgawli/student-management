package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.response.DepartmentDto;
import com.aurionpro.studentmanagement.mapper.DepartmentMapper;
import com.aurionpro.studentmanagement.repository.DepartmentRepository;
import com.aurionpro.studentmanagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link DepartmentService} interface.
 * Handles the business logic for operations related to academic departments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    /**
     * Retrieves a complete list of all academic departments from the database.
     * This operation is executed within a read-only transaction to improve performance,
     * as no data modifications are made.
     *
     * @return A list of {@link DepartmentDto} objects, where each object represents a
     *         single department. If no departments exist, an empty list is returned.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartments() {
        log.info("Fetching all departments from the database.");
        List<DepartmentDto> departments = departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
        log.info("Found {} departments.", departments.size());
        return departments;
    }
}