package com.aurionpro.studentmanagement.mapper;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

/**
 * A MapStruct mapper for handling conversions between Student-related DTOs and the {@link Student} entity.
 * It uses {@link DepartmentMapper} and {@link CourseMapper} for converting nested objects.
 */
@Mapper(componentModel = "spring", uses = {DepartmentMapper.class, CourseMapper.class})
public interface StudentMapper {

    /**
     * Converts a {@link Student} entity to a {@link StudentResponseDto}.
     * This mapping is straightforward as field names match. Nested objects (Department, Courses)
     * are handled by the mappers specified in the `uses` attribute of @Mapper.
     *
     * @param student The student entity to convert.
     * @return The resulting DTO containing the student's full details.
     */
    StudentResponseDto toDto(Student student);

    /**
     * Maps a {@link CreateStudentRequestDto} to a new {@link Student} entity.
     * Ignores fields that are either database-generated (id, createdAt, updatedAt),
     * system-managed (isActive), or handled manually in the service layer (department, courses).
     *
     * @param dto The DTO containing the data for the new student.
     * @return A new Student entity populated with data from the DTO.
     */
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "department", ignore = true),
        @Mapping(target = "courses", ignore = true),
        @Mapping(target = "active", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    Student toEntity(CreateStudentRequestDto dto);


    /**
     * Updates an existing {@link Student} entity from an {@link UpdateStudentRequestDto}.
     * This method modifies the entity passed in the {@code @MappingTarget} parameter.
     * It ignores immutable fields (id, studentId), system-managed fields, and complex objects
     * that are updated separately in the service logic.
     *
     * @param dto The DTO with the updated student information.
     * @param entity The existing Student entity to be updated.
     */
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