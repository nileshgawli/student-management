package com.aurionpro.studentmanagement.service;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

	Page<StudentResponseDto> getAllStudents(String filter, Boolean isActive, Pageable pageable);

	StudentResponseDto addStudent(CreateStudentRequestDto requestDto);

	StudentResponseDto updateStudent(String studentId, UpdateStudentRequestDto requestDto);

	void softDeleteStudent(String studentId);

	StudentResponseDto toggleStudentStatus(String studentId);
}