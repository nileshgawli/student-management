package com.aurionpro.studentmanagement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "students")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class) // Enable JPA Auditing
@Schema(description = "Represents a student enrolled in the institution.")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Auto-generated unique database identifier.", example = "1")
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true, length = 100)
    @Schema(description = "Unique business ID of the student.", example = "S001")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "department", nullable = false)
    @Schema(description = "Department in which the student is enrolled.", example = "COMPUTER_SCIENCE")
    private Department department;

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