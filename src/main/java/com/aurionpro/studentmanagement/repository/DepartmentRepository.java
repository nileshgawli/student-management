package com.aurionpro.studentmanagement.repository;

import com.aurionpro.studentmanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Department} entities.
 * Provides standard database operations (CRUD) for the Department entity.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}