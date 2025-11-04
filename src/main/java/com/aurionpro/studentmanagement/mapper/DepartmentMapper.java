package com.aurionpro.studentmanagement.mapper;

import com.aurionpro.studentmanagement.dto.response.DepartmentDetailDto;
import com.aurionpro.studentmanagement.dto.response.DepartmentDto;
import com.aurionpro.studentmanagement.entity.Department;
import org.mapstruct.Mapper;

/**
 * A MapStruct mapper for converting {@link Department} entities to various Department DTOs.
 * It uses {@link CourseMapper} for converting nested Course entities.
 * This component is managed by Spring.
 */
@Mapper(componentModel = "spring", uses = {CourseMapper.class})
public interface DepartmentMapper {

    /**
     * Converts a {@link Department} entity into a basic {@link DepartmentDto}.
     * This DTO contains only the core information like ID and name.
     *
     * @param department The Department entity to be converted.
     * @return The corresponding DepartmentDto.
     */
    DepartmentDto toDto(Department department);

    /**
     * Converts a {@link Department} entity into a {@link DepartmentDetailDto}.
     * This DTO provides a more comprehensive view, including the department's
     * active status and its full list of associated courses. The nested conversion
     * of {@code List<Course>} to {@code List<CourseDto>} is handled automatically
     * by the {@code CourseMapper} specified in the {@code @Mapper(uses = ...)} annotation.
     *
     * @param department The Department entity to be converted.
     * @return The corresponding DepartmentDetailDto.
     */
    DepartmentDetailDto toDetailDto(Department department);
}