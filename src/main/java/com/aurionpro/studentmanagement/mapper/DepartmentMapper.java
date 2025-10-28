package com.aurionpro.studentmanagement.mapper;

import com.aurionpro.studentmanagement.dto.response.DepartmentDto;
import com.aurionpro.studentmanagement.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentDto toDto(Department department);
}