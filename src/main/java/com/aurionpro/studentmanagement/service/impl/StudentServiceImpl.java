package com.aurionpro.studentmanagement.service.impl;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.entity.Student;
import com.aurionpro.studentmanagement.exception.DuplicateResourceException;
import com.aurionpro.studentmanagement.exception.ResourceNotFoundException;
import com.aurionpro.studentmanagement.mapper.StudentMapper;
import com.aurionpro.studentmanagement.repository.StudentRepository;
import com.aurionpro.studentmanagement.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

	private final StudentRepository studentRepository;
	private final StudentMapper studentMapper;

	public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper) {
		this.studentRepository = studentRepository;
		this.studentMapper = studentMapper;
	}

	@Override
	@Transactional
	public StudentResponseDto addStudent(CreateStudentRequestDto requestDto) {
		if (studentRepository.existsByStudentId(requestDto.getStudentId())) {
			throw new DuplicateResourceException(
					"A student with ID '" + requestDto.getStudentId() + "' already exists.");
		}

		if (studentRepository.existsByEmail(requestDto.getEmail())) {
			throw new DuplicateResourceException(
					"A student with email '" + requestDto.getEmail() + "' already exists.");
		}

		Student student = studentMapper.toEntity(requestDto);
		student.setCreatedAt(Instant.now());
		Student savedStudent = studentRepository.save(student);

		return studentMapper.toDto(savedStudent);
	}

	@Override
	@Transactional
	public StudentResponseDto updateStudent(String studentId, UpdateStudentRequestDto requestDto) {
		Student existingStudent = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

		String newEmail = requestDto.getEmail();
		String currentEmail = existingStudent.getEmail();

		if (newEmail != null && !newEmail.equalsIgnoreCase(currentEmail)) {
			if (studentRepository.existsByEmail(newEmail)) {
				throw new DuplicateResourceException("Email '" + newEmail + "' is already in use by another student.");
			}
		}

		studentMapper.updateEntityFromDto(requestDto, existingStudent);
		existingStudent.setUpdatedAt(Instant.now());

		Student updatedStudent = studentRepository.save(existingStudent);
		return studentMapper.toDto(updatedStudent);
	}

	@Override
	public Page<StudentResponseDto> getAllActiveStudents(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Student> studentPage = studentRepository.findAllByIsActiveTrue(pageable);
		return studentPage.map(studentMapper::toDto);
	}

	@Override
	@Transactional
	public void softDeleteStudent(String studentId) {
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

		student.setActive(false);
		student.setUpdatedAt(Instant.now());
		studentRepository.save(student);
	}
}