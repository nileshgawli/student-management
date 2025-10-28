package com.aurionpro.studentmanagement.repository;

import com.aurionpro.studentmanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds all courses associated with a specific department ID.
     * @param departmentId The ID of the department.
     * @return A list of courses offered by the department.
     */
    List<Course> findByDepartmentId(Long departmentId);
}