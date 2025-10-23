package com.aurionpro.studentmanagement.mapper;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.entity.Student;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    private final ModelMapper modelMapper;

    public StudentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public StudentResponseDto toDto(Student student) {
        return modelMapper.map(student, StudentResponseDto.class);
    }

    public Student toEntity(CreateStudentRequestDto dto) {
        return modelMapper.map(dto, Student.class);
    }

    public void updateEntityFromDto(UpdateStudentRequestDto dto, Student entity) {
        modelMapper.map(dto, entity);
    }
}