package com.aurionpro.studentmanagement.config;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.entity.Student;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<CreateStudentRequestDto, Student> typeMap = modelMapper.createTypeMap(CreateStudentRequestDto.class, Student.class);

        typeMap.addMappings(mapper -> mapper.skip(Student::setId));

        return modelMapper;
    }
} 