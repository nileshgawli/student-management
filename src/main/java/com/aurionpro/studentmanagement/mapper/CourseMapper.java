package com.aurionpro.studentmanagement.mapper;

import com.aurionpro.studentmanagement.dto.response.CourseDto;
import com.aurionpro.studentmanagement.entity.Course;
import org.mapstruct.Mapper;

/**
 * A MapStruct mapper for converting {@link Course} entities to {@link CourseDto} data transfer objects.
 * This component is managed by Spring and can be injected into other services.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper {

    /**
     * Converts a {@link Course} entity into a {@link CourseDto}.
     *
     * @param course The Course entity to be converted.
     * @return The corresponding CourseDto.
     */
    CourseDto toDto(Course course);
}