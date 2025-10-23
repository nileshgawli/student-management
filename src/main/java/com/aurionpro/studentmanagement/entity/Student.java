package com.aurionpro.studentmanagement.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
@Schema(description = "Represents a student enrolled in the institution.")
public class Student {

    @Id
    @Column(name = "student_id", nullable = false, unique = true)
    @Schema(description = "Unique ID of the student.", example = "S001")
    private String studentId;

    @Column(name = "first_name", nullable = false)
    @Schema(description = "First name of the student.", example = "Nilesh")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Schema(description = "Last name of the student.", example = "Gawli")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Email address of the student.", example = "nilesh.gawli@example.com")
    private String email;

    @Column(name = "department", nullable = false)
    @Schema(description = "Department in which the student is enrolled.", example = "Computer Science")
    private String department;

    @Column(name = "year", nullable = false)
    @Schema(description = "Academic year of the student.", example = "Third Year")
    private String year;

    @Column(name = "is_active", nullable = false)
    @Schema(description = "Indicates if the student record is active.", example = "true")
    private boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "UTC timestamp when the student record was created.")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Schema(description = "UTC timestamp when the student record was last updated.")
    private Instant updatedAt;
}
