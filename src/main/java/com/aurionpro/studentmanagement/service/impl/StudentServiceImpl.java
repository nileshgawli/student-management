package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import com.aurionpro.studentmanagement.entity.Course;
import com.aurionpro.studentmanagement.entity.Department;
import com.aurionpro.studentmanagement.entity.Student;
import com.aurionpro.studentmanagement.exception.BusinessRuleException;
import com.aurionpro.studentmanagement.exception.DuplicateResourceException;
import com.aurionpro.studentmanagement.exception.ResourceNotFoundException;
import com.aurionpro.studentmanagement.exception.ValidationException;
import com.aurionpro.studentmanagement.mapper.StudentMapper;
import com.aurionpro.studentmanagement.repository.CourseRepository;
import com.aurionpro.studentmanagement.repository.DepartmentRepository;
import com.aurionpro.studentmanagement.repository.StudentRepository;
import com.aurionpro.studentmanagement.service.StudentExportService;
import com.aurionpro.studentmanagement.service.StudentService;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link StudentService} interface.
 * This class orchestrates the data validation, entity mapping, and repository interactions
 * for all student-related business logic. All database operations are transactional.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    /**
     * A private helper record to encapsulate the successfully validated
     * Department and Course entities, making them easy to pass between methods.
     */
    private record ValidatedEntities(Department department, Set<Course> courses) {}

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;
    private final StudentExportService studentExportService;

    @Override
    @Transactional
    public StudentResponseDto addStudent(CreateStudentRequestDto requestDto) {
        log.info("Attempting to add a new student with studentId: {}", requestDto.getStudentId());

        if (studentRepository.existsByStudentId(requestDto.getStudentId())) {
            log.warn("Failed to add student. Duplicate studentId: {}", requestDto.getStudentId());
            throw new DuplicateResourceException("A student with ID '" + requestDto.getStudentId() + "' already exists.");
        }
        
        ValidatedEntities validated = validateStudentData(null, requestDto.getEmail(), requestDto.getDepartmentId(), requestDto.getCourseIds());
        log.debug("Student data validation successful for studentId: {}", requestDto.getStudentId());

        Student student = studentMapper.toEntity(requestDto);
        student.setDepartment(validated.department());
        student.setCourses(validated.courses());
        student.setActive(true);
        
        Student savedStudent = studentRepository.save(student);
        log.info("Successfully added new student with database ID: {} and studentId: {}", savedStudent.getId(), savedStudent.getStudentId());
        
        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudent(String studentId, UpdateStudentRequestDto requestDto) {
        log.info("Attempting to update student with studentId: {}", studentId);

        Student existingStudent = findStudentByBusinessId(studentId);
        log.debug("Found student to update with database ID: {}", existingStudent.getId());
        
        ValidatedEntities validated = validateStudentData(existingStudent.getEmail(), requestDto.getEmail(), requestDto.getDepartmentId(), requestDto.getCourseIds());
        log.debug("Student data validation successful for update of studentId: {}", studentId);

        studentMapper.updateEntityFromDto(requestDto, existingStudent);
        existingStudent.setDepartment(validated.department());
        existingStudent.setCourses(validated.courses());

        Student updatedStudent = studentRepository.save(existingStudent);
        log.info("Successfully updated student with studentId: {}", updatedStudent.getStudentId());

        return studentMapper.toDto(updatedStudent);
    }

    /**
     * A central validation method for student data, used in both create and update operations.
     * It performs the following checks:
     * 1.  Ensures the new email is not already used by another student.
     * 2.  Verifies that the specified department exists and is active.
     * 3.  Verifies that all specified courses exist and are active.
     * 4.  Ensures that all specified courses belong to the specified department.
     *
     * @param currentEmail The current email of the student being updated, or null if creating a new student.
     * @param newEmail     The new email address to be validated.
     * @param departmentId The ID of the department to validate.
     * @param courseIds    A list of course IDs to validate.
     * @return A {@link ValidatedEntities} record containing the fetched Department and a Set of Courses.
     * @throws ValidationException if any validation rule fails, containing a list of all errors.
     * @throws BusinessRuleException if a business rule (like assigning to an inactive department) is violated.
     */
    private ValidatedEntities validateStudentData(String currentEmail, String newEmail, Long departmentId, List<Long> courseIds) {
        log.debug("Initiating validation for email: {}, departmentId: {}", newEmail, departmentId);
        List<String> errors = new ArrayList<>();

        if ((currentEmail == null || !currentEmail.equalsIgnoreCase(newEmail)) && studentRepository.existsByEmail(newEmail)) {
            errors.add("Email '" + newEmail + "' is already in use by another student.");
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessRuleException("Department with ID '" + departmentId + "' does not exist."));
        
        if (!department.isActive()) {
            throw new BusinessRuleException("Cannot assign student to an inactive department: " + department.getName());
        }

        Set<Course> courses = new HashSet<>();
        if (!CollectionUtils.isEmpty(courseIds)) {
            List<Course> foundCourses = courseRepository.findByIdInWithDepartment(courseIds);
            
            if (foundCourses.size() != courseIds.size()) {
                Set<Long> foundCourseIds = foundCourses.stream().map(Course::getId).collect(Collectors.toSet());
                List<Long> missingIds = courseIds.stream().filter(id -> !foundCourseIds.contains(id)).collect(Collectors.toList());
                errors.add("The following course IDs do not exist: " + missingIds);
            }

            for (Course course : foundCourses) {
                if (!course.isActive()) {
                     errors.add("Course '" + course.getName() + "' is inactive and cannot be assigned.");
                }
                if (!course.getDepartment().getId().equals(department.getId())) {
                    errors.add("Course '" + course.getName() + "' belongs to the '" + course.getDepartment().getName() + "' department, not the '" + department.getName() + "' department.");
                }
            }
            courses.addAll(foundCourses);
        }

        if (!errors.isEmpty()) {
            log.warn("Validation failed with {} errors: {}", errors.size(), errors);
            throw new ValidationException("Student data is invalid. Please correct the following issues.", errors);
        }
        log.debug("Validation successful.");
        return new ValidatedEntities(department, courses);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDto> getAllStudents(String filter, Boolean isActive, Pageable pageable) {
        log.info("Fetching students page number: {}, page size: {}, filter: '{}', isActive: {}",
            pageable.getPageNumber(), pageable.getPageSize(), filter, isActive);
        
        Specification<Student> spec = createSpecification(filter, isActive);
        Page<Student> studentPage = studentRepository.findAll(spec, pageable);

        log.info("Found {} students on page {}", studentPage.getNumberOfElements(), pageable.getPageNumber());
        return studentPage.map(studentMapper::toDto);
    }

    @Override
    @Transactional
    public void softDeleteStudent(String studentId) {
        log.info("Attempting to soft delete student with studentId: {}", studentId);
        Student student = findStudentByBusinessId(studentId);
        student.setActive(false);
        studentRepository.save(student);
        log.info("Successfully soft-deleted student with studentId: {}", studentId);
    }

    @Override
    @Transactional
    public StudentResponseDto toggleStudentStatus(String studentId) {
        log.info("Attempting to toggle status for student with studentId: {}", studentId);
        Student student = findStudentByBusinessId(studentId);
        boolean currentStatus = student.isActive();
        student.setActive(!currentStatus);
        Student updatedStudent = studentRepository.save(student);
        log.info("Successfully toggled status for studentId: {} from {} to {}", studentId, currentStatus, updatedStudent.isActive());
        return studentMapper.toDto(updatedStudent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public void generateStudentsExcel(String filter, Boolean isActive, HttpServletResponse response) throws IOException {
        log.info("Generating Excel report with filter: '{}', isActive: {}", filter, isActive);
        Specification<Student> spec = createSpecification(filter, isActive);
        List<Student> students = studentRepository.findAll(spec, Sort.by("id"));
        studentExportService.exportToExcel(students, response);
    }

    @Override
    @Transactional(readOnly = true)
    public void generateStudentsCsv(String filter, Boolean isActive, HttpServletResponse response) throws IOException {
        log.info("Generating CSV report with filter: '{}', isActive: {}", filter, isActive);
        Specification<Student> spec = createSpecification(filter, isActive);
        List<Student> students = studentRepository.findAll(spec, Sort.by("id"));
        studentExportService.exportToCsv(students, response);
    }
    
    private Student findStudentByBusinessId(String studentId) {
        log.debug("Searching for student with studentId: {}", studentId);
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    log.warn("Student not found with studentId: {}", studentId);
                    return new ResourceNotFoundException("Student not found with ID: " + studentId);
                });
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