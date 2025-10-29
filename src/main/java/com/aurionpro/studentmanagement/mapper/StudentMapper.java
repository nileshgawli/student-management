package com.aurionpro.studentmanagement.mapper;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class, CourseMapper.class}) // Add this
public interface StudentMapper {

    StudentResponseDto toDto(Student student);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "department", ignore = true),
        @Mapping(target = "courses", ignore = true),
        @Mapping(target = "active", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    Student toEntity(CreateStudentRequestDto dto);


    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "studentId", ignore = true),
        @Mapping(target = "department", ignore = true),
        @Mapping(target = "courses", ignore = true),
        @Mapping(target = "active", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    void updateEntityFromDto(UpdateStudentRequestDto dto, @MappingTarget Student entity);
}