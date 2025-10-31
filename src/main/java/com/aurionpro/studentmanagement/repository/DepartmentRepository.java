package com.aurionpro.studentmanagement.repository;

import com.aurionpro.studentmanagement.entity.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Department} entities.
 * Provides standard database operations (CRUD) for the Department entity.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {
    boolean existsByName(String name);
    List<Department> findByIsActive(boolean isActive);
}