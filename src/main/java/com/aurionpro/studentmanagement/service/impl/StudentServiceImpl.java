package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.entity.Student;
import com.aurionpro.studentmanagement.exception.DuplicateResourceException;
import com.aurionpro.studentmanagement.exception.ResourceNotFoundException;
import com.aurionpro.studentmanagement.mapper.StudentMapper;
import com.aurionpro.studentmanagement.repository.StudentRepository;
import com.aurionpro.studentmanagement.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    public List<StudentResponseDto> getAllActiveStudents() {
        log.info("Fetching all active students from the database.");
        List<Student> students = studentRepository.findAllByIsActiveTrue();
        return students.stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDto addStudent(CreateStudentRequestDto requestDto) {
        log.info("Attempting to add a new student with ID: {}", requestDto.getStudentId());

        if (studentRepository.existsById(requestDto.getStudentId())) {
            log.warn("Student with ID {} already exists. Throwing DuplicateResourceException.", requestDto.getStudentId());
            throw new DuplicateResourceException("Student with ID " + requestDto.getStudentId() + " already exists.");
        }

        Student student = studentMapper.toEntity(requestDto);
        student.setActive(true); // Business rule: new students are always active.

        Student savedStudent = studentRepository.save(student);
        log.info("Successfully added student with ID: {}", savedStudent.getStudentId());

        return studentMapper.toDto(savedStudent);
    }

    @Override
    public StudentResponseDto updateStudent(String studentId, UpdateStudentRequestDto requestDto) {
        log.info("Attempting to update student with ID: {}", studentId);

        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("Failed to find student with ID: {}. Throwing ResourceNotFoundException.", studentId);
                    return new ResourceNotFoundException("Student not found with ID: " + studentId);
                });

        studentMapper.updateEntityFromDto(requestDto, existingStudent);
        Student updatedStudent = studentRepository.save(existingStudent);
        log.info("Successfully updated student with ID: {}", updatedStudent.getStudentId());

        return studentMapper.toDto(updatedStudent);
    }

    @Override
    public void softDeleteStudent(String id) {
        log.info("Attempting to soft-delete student with ID: {}", id);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to find student with ID: {}. Cannot perform delete. Throwing ResourceNotFoundException.", id);
                    return new ResourceNotFoundException("Student not found with ID: " + id);
                });

        student.setActive(false);
        studentRepository.save(student);
        log.info("Successfully soft-deleted student with ID: {}", id);
    }
}