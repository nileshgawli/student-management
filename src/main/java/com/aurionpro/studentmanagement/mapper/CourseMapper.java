package com.aurionpro.studentmanagement.mapper;

import com.aurionpro.studentmanagement.dto.response.CourseDto;
import com.aurionpro.studentmanagement.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDto toDto(Course course);
}