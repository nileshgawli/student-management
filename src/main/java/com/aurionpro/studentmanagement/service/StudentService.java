package com.aurionpro.studentmanagement.service;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import org.springframework.data.domain.Page;

public interface StudentService {

    Page<StudentResponseDto> getAllActiveStudents(int page, int size);
    StudentResponseDto addStudent(CreateStudentRequestDto requestDto);
    StudentResponseDto updateStudent(String studentId, UpdateStudentRequestDto requestDto);
    void softDeleteStudent(String id);
}