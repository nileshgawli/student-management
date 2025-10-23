package com.aurionpro.studentmanagement.service;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import java.util.List;

public interface StudentService {

    List<StudentResponseDto> getAllActiveStudents();
    StudentResponseDto addStudent(CreateStudentRequestDto requestDto);
    StudentResponseDto updateStudent(String studentId, UpdateStudentRequestDto requestDto);
    void softDeleteStudent(String id);
}