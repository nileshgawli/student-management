package com.aurionpro.studentmanagement.repository;

import com.aurionpro.studentmanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByDepartmentId(Long departmentId);

    /**
     * Finds all courses by their IDs and eagerly fetches their associated Department entities
     * in a single query to prevent the N+1 problem.
     * @param courseIds The list of course IDs to fetch.
     * @return A list of Courses with their Departments pre-loaded.
     */
    @Query("SELECT c FROM Course c JOIN FETCH c.department WHERE c.id IN :courseIds")
    List<Course> findByIdInWithDepartment(@Param("courseIds") List<Long> courseIds);
}