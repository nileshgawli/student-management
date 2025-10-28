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

    // This handles the Student -> StudentResponseDto mapping automatically
    // because the field names (department, courses) match.
    StudentResponseDto toDto(Student student);

    @Mappings({
        // We explicitly ignore departmentId and courseIds because they don't exist on the Student entity.
        // We handle mapping these to entities in the service layer.
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "department", ignore = true),
        @Mapping(target = "courses", ignore = true),
        @Mapping(target = "active", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    Student toEntity(CreateStudentRequestDto dto);


    @Mappings({
        // Similar to above, we ignore fields that aren't meant to be updated directly from the DTO.
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