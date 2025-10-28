package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.entity.Course;
import com.aurionpro.studentmanagement.entity.Department;
import com.aurionpro.studentmanagement.entity.Student;
import com.aurionpro.studentmanagement.exception.DuplicateResourceException;
import com.aurionpro.studentmanagement.exception.ResourceNotFoundException;
import com.aurionpro.studentmanagement.exception.ValidationException;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public StudentResponseDto addStudent(CreateStudentRequestDto requestDto) {
        if (studentRepository.existsByStudentId(requestDto.getStudentId())) {
            throw new DuplicateResourceException("A student with ID '" + requestDto.getStudentId() + "' already exists.");
        }
        
        ValidatedEntities validated = validateStudentData(null, requestDto.getEmail(), requestDto.getDepartmentId(), requestDto.getCourseIds());

        Student student = studentMapper.toEntity(requestDto);
        student.setDepartment(validated.department());
        student.setCourses(validated.courses());
        student.setActive(true);
        
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudent(String studentId, UpdateStudentRequestDto requestDto) {
        Student existingStudent = findStudentByBusinessId(studentId);
        
        ValidatedEntities validated = validateStudentData(existingStudent.getEmail(), requestDto.getEmail(), requestDto.getDepartmentId(), requestDto.getCourseIds());

        studentMapper.updateEntityFromDto(requestDto, existingStudent);
        existingStudent.setDepartment(validated.department());
        existingStudent.setCourses(validated.courses());

        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toDto(updatedStudent);
    }

    private record ValidatedEntities(Department department, Set<Course> courses) {}

    private ValidatedEntities validateStudentData(String currentEmail, String newEmail, Long departmentId, List<Long> courseIds) {
        List<String> errors = new ArrayList<>();

        if ((currentEmail == null || !currentEmail.equalsIgnoreCase(newEmail)) && studentRepository.existsByEmail(newEmail)) {
            errors.add("Email '" + newEmail + "' is already in use by another student.");
        }

        Department department = departmentRepository.findById(departmentId)
                .orElse(null);
        if (department == null) {
            errors.add("Department with ID '" + departmentId + "' does not exist.");
        }

        Set<Course> courses = new HashSet<>();
        if (!CollectionUtils.isEmpty(courseIds)) {
            // Use the new, hyper-efficient query to fetch courses and their departments in one go.
            List<Course> foundCourses = courseRepository.findByIdInWithDepartment(courseIds);
            
            // 1. Check for any courses that were requested but not found.
            if (foundCourses.size() != courseIds.size()) {
                Set<Long> foundCourseIds = foundCourses.stream().map(Course::getId).collect(Collectors.toSet());
                List<Long> missingIds = courseIds.stream()
                        .filter(id -> !foundCourseIds.contains(id))
                        .toList();
                errors.add("The following course IDs do not exist: " + missingIds);
            }

            // 2. If the department is valid, check the business rule.
            // This is now safe and fast because the department data for each course is already loaded.
            if (department != null) {
                for (Course course : foundCourses) {
                    if (!course.getDepartment().getId().equals(department.getId())) {
                        errors.add("Course '" + course.getName() + "' belongs to the '" + course.getDepartment().getName() + "' department, not the '" + department.getName() + "' department.");
                    }
                }
            }
            courses.addAll(foundCourses);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Student data is invalid. Please correct the following issues.", errors);
        }

        // We can only reach here if department is not null, so this is safe.
        return new ValidatedEntities(department, courses);
    }
    
    // ... other methods (getAllStudents, softDelete, toggleStatus, etc.) remain unchanged ...

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDto> getAllStudents(String filter, Boolean isActive, Pageable pageable) {
        Specification<Student> spec = createSpecification(filter, isActive);
        Page<Student> studentPage = studentRepository.findAll(spec, pageable);
        return studentPage.map(studentMapper::toDto);
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
        student.setActive(!student.isActive());
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