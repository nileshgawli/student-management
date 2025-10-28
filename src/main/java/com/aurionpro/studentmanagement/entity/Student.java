package com.aurionpro.studentmanagement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @Schema(description = "The department to which the student is assigned.")
    private Department department;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Schema(description = "The set of courses in which the student is enrolled.")
    private Set<Course> courses = new HashSet<>();

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