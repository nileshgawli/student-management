package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.response.CourseDto;
import com.aurionpro.studentmanagement.entity.Course;
import com.aurionpro.studentmanagement.mapper.CourseMapper;
import com.aurionpro.studentmanagement.repository.CourseRepository;
import com.aurionpro.studentmanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper; 

    @Override
    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses(Long departmentId) {
        List<Course> courses;
        if (departmentId != null) {
            courses = courseRepository.findByDepartmentId(departmentId);
        } else {
            courses = courseRepository.findAll();
        }
        
        return courses.stream()
                .map(courseMapper::toDto) 
                .collect(Collectors.toList());
    }
}