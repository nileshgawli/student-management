package com.aurionpro.studentmanagement.repository;

import com.aurionpro.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Student} entities.
 * It extends {@link JpaRepository} for standard CRUD operations and
 * {@link JpaSpecificationExecutor} to enable dynamic, criteria-based queries.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    /**
     * Checks if a student exists with the given business ID (studentId).
     *
     * @param studentId The business ID to check for.
     * @return {@code true} if a student with this ID exists, {@code false} otherwise.
     */
    boolean existsByStudentId(String studentId);

    /**
     * Checks if a student exists with the given email address.
     *
     * @param email The email address to check for.
     * @return {@code true} if a student with this email exists, {@code false} otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves a student by their unique business ID (studentId).
     *
     * @param studentId The business ID of the student to find.
     * @return An {@link Optional} containing the found {@link Student}, or an empty Optional if no student is found.
     */
    Optional<Student> findByStudentId(String studentId);
}