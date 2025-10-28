package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.entity.Course;
import com.aurionpro.studentmanagement.entity.Department;
import com.aurionpro.studentmanagement.entity.Student;
import com.aurionpro.studentmanagement.exception.DuplicateResourceException;
import com.aurionpro.studentmanagement.exception.ResourceNotFoundException;
import com.aurionpro.studentmanagement.mapper.StudentMapper;
import com.aurionpro.studentmanagement.repository.CourseRepository;
import com.aurionpro.studentmanagement.repository.DepartmentRepository;
import com.aurionpro.studentmanagement.repository.StudentRepository;
import com.aurionpro.studentmanagement.service.StudentService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDto> getAllStudents(String filter, Boolean isActive, Pageable pageable) {
        Specification<Student> spec = createSpecification(filter, isActive);
        Page<Student> studentPage = studentRepository.findAll(spec, pageable);
        return studentPage.map(studentMapper::toDto);
    }

    @Override
    @Transactional
    public StudentResponseDto addStudent(CreateStudentRequestDto requestDto) {
        if (studentRepository.existsByStudentId(requestDto.getStudentId())) {
            throw new DuplicateResourceException("A student with ID '" + requestDto.getStudentId() + "' already exists.");
        }
        if (studentRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("A student with email '" + requestDto.getEmail() + "' already exists.");
        }

        Student student = studentMapper.toEntity(requestDto);
        
        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + requestDto.getDepartmentId()));
        student.setDepartment(department);

        if (requestDto.getCourseIds() != null && !requestDto.getCourseIds().isEmpty()) {
            List<Course> courses = courseRepository.findAllById(requestDto.getCourseIds());
            if (courses.size() != requestDto.getCourseIds().size()) {
                 throw new ResourceNotFoundException("One or more courses not found.");
            }
            student.setCourses(new HashSet<>(courses));
        }

        student.setActive(true);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudent(String studentId, UpdateStudentRequestDto requestDto) {
        Student existingStudent = findStudentByBusinessId(studentId);

        String newEmail = requestDto.getEmail();
        if (!newEmail.equalsIgnoreCase(existingStudent.getEmail())) {
            if (studentRepository.existsByEmail(newEmail)) {
                throw new DuplicateResourceException("Email '" + newEmail + "' is already in use by another student.");
            }
        }

        studentMapper.updateEntityFromDto(requestDto, existingStudent);

        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + requestDto.getDepartmentId()));
        existingStudent.setDepartment(department);
        
        Set<Course> courses = new HashSet<>();
        if (requestDto.getCourseIds() != null && !requestDto.getCourseIds().isEmpty()) {
             List<Course> foundCourses = courseRepository.findAllById(requestDto.getCourseIds());
             if (foundCourses.size() != requestDto.getCourseIds().size()) {
                 throw new ResourceNotFoundException("One or more courses not found.");
            }
            courses.addAll(foundCourses);
        }
        existingStudent.setCourses(courses);

        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toDto(updatedStudent);
    }
    
    @Override
    @Transactional
    public void softDeleteStudent(String studentId) {
        Student student = findStudentByBusinessId(studentId);
        student.setActive(false);
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public StudentResponseDto toggleStudentStatus(String studentId) {
        Student student = findStudentByBusinessId(studentId);
        student.setActive(!student.isActive()); // Invert the current status
        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toDto(updatedStudent);
    }

    private Student findStudentByBusinessId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }

    private Specification<Student> createSpecification(String filter, Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> mainPredicates = new ArrayList<>();

            if (isActive != null) {
                mainPredicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            if (StringUtils.hasText(filter)) {
                String pattern = "%" + filter.toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("studentId")), pattern));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern));
                mainPredicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
            }

            return criteriaBuilder.and(mainPredicates.toArray(new Predicate[0]));
        };
    }
}