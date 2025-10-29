package com.aurionpro.studentmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aurionpro.studentmanagement.entity.Course;

/**
 * Spring Data JPA repository for {@link Course} entities.
 * This interface provides standard CRUD operations and allows for custom query definitions.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds all courses associated with a specific department ID.
     *
     * @param departmentId The ID of the department to search for courses in.
     * @return A list of {@link Course} entities belonging to the specified department.
     */
    List<Course> findByDepartmentId(Long departmentId);

    /**
     * Finds all courses by their IDs and eagerly fetches their associated Department entities
     * in a single query to prevent the N+1 problem. This is more efficient than fetching
     * each department separately.
     *
     * @param courseIds The list of course IDs to fetch.
     * @return A list of Courses with their Departments pre-loaded.
     */
    @Query("SELECT c FROM Course c JOIN FETCH c.department WHERE c.id IN :courseIds")
    List<Course> findByIdInWithDepartment(@Param("courseIds") List<Long> courseIds);
}