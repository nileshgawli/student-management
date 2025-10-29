package com.aurionpro.studentmanagement.mapper;

import com.aurionpro.studentmanagement.dto.response.DepartmentDto;
import com.aurionpro.studentmanagement.entity.Department;
import org.mapstruct.Mapper;

/**
 * A MapStruct mapper for converting {@link Department} entities to {@link DepartmentDto} data transfer objects.
 * This component is managed by Spring.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    /**
     * Converts a {@link Department} entity into a {@link DepartmentDto}.
     *
     * @param department The Department entity to be converted.
     * @return The corresponding DepartmentDto.
     */
    DepartmentDto toDto(Department department);
}