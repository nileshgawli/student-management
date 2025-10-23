package com.aurionpro.studentmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aurionpro.studentmanagement.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    // Find all students where isActive is true
    List<Student> findAllByIsActiveTrue();

    // Check if a student with the given ID exists
    boolean existsByStudentId(String studentId);
    
    // Check if a student with the given emaikl exits
    boolean existsByEmail(String email);

}