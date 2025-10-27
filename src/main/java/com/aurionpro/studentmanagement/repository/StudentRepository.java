package com.aurionpro.studentmanagement.repository;

import com.aurionpro.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    boolean existsByStudentId(String studentId);

    boolean existsByEmail(String email);

    Optional<Student> findByStudentId(String studentId);
}