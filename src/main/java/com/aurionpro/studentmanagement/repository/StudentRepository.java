package com.aurionpro.studentmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aurionpro.studentmanagement.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    // Find all students where isActive is true (existing method)
    List<Student> findAllByIsActiveTrue();

    // NEW: Find all active students with pagination
    Page<Student> findAllByIsActiveTrue(Pageable pageable);

    // Check if a student with the given ID exists
    boolean existsByStudentId(String studentId);
    
    // Check if a student with the given emaikl exits
    boolean existsByEmail(String email);

}