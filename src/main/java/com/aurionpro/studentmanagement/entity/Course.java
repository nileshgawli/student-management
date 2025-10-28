package com.aurionpro.studentmanagement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "courses")
@Getter
@Setter
@Schema(description = "Represents a course offered by a department.")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Auto-generated unique database identifier.", example = "101")
    private Long id;

    @Column(name = "name", nullable = false)
    @Schema(description = "The name of the course.", example = "Data Structures and Algorithms")
    private String name;

    @Column(name = "description")
    @Schema(description = "A brief description of the course content.")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @Schema(description = "The department that offers this course.")
    private Department department;
}