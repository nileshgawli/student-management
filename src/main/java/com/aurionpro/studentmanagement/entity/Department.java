package com.aurionpro.studentmanagement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "departments")
@Getter
@Setter
@Schema(description = "Represents an academic department in the institution.")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Auto-generated unique database identifier.", example = "1")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @Schema(description = "The name of the department.", example = "COMPUTER_SCIENCE")
    private String name;
}