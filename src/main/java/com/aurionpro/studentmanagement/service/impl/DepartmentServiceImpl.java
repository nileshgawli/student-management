package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.response.DepartmentDto;
import com.aurionpro.studentmanagement.mapper.DepartmentMapper;
import com.aurionpro.studentmanagement.repository.DepartmentRepository;
import com.aurionpro.studentmanagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

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