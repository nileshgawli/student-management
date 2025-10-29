package com.aurionpro.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Student Management System application.
 * This class is responsible for bootstrapping the Spring Boot application.
 *
 * <p>The {@link SpringBootApplication} annotation enables auto-configuration,
 * component scanning, and property support.
 *
 * <p>The {@link EnableJpaAuditing} annotation activates Spring Data JPA's
 * auditing feature, which automatically populates the {@code createdAt} and
 * {@code updatedAt} fields in entities like {@code Student}.
 */
@SpringBootApplication
@EnableJpaAuditing
public class StudentManagementApplication {

	/**
	 * The main method which serves as the entry point for the Java Virtual Machine (JVM)
	 * to start the application. It delegates to Spring Boot's {@link SpringApplication}
	 * class to launch the application.
	 *
	 * @param args Command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(StudentManagementApplication.class, args);
	}
	
}