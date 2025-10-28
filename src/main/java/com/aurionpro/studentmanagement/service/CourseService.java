package com.aurionpro.studentmanagement.service;

import com.aurionpro.studentmanagement.dto.response.CourseDto;

import java.util.List;

public interface CourseService {
    List<CourseDto> getAllCourses(Long departmentId);
}