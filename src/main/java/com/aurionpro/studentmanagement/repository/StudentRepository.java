package com.aurionpro.studentmanagement.repository;

import com.aurionpro.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    // Find all students where isActive is true
    List<Student> findAllByIsActiveTrue();

    // Check if a student with the given ID exists
    boolean existsByStudentId(String studentId);
}