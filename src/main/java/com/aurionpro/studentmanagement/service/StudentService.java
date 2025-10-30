package com.aurionpro.studentmanagement.service;

import com.aurionpro.studentmanagement.dto.request.CreateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.request.UpdateStudentRequestDto;
import com.aurionpro.studentmanagement.dto.response.StudentResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface defining the business logic for student management.
 * This contract outlines all operations related to creating, retrieving,
 * updating, and deactivating student records.
 */
public interface StudentService {

	/**
	 * Retrieves a paginated and filtered list of students.
	 *
	 * @param filter   A string used for a broad search across multiple fields
	 *                 like student ID, first name, last name, and email. Can be null.
	 * @param isActive A boolean to filter students based on their active status.
	 *                 Can be null to include both active and inactive students.
	 * @param pageable An object containing pagination and sorting information.
	 * @return A {@link Page} of {@link StudentResponseDto} objects matching the criteria.
	 */
	Page<StudentResponseDto> getAllStudents(String filter, Boolean isActive, Pageable pageable);

	/**
	 * Creates a new student record based on the provided data.
	 *
	 * @param requestDto The DTO containing the details for the new student.
	 * @return A {@link StudentResponseDto} representing the newly created student.
	 * @throws com.aurionpro.studentmanagement.exception.DuplicateResourceException if the student ID or email already exists.
	 * @throws com.aurionpro.studentmanagement.exception.ValidationException if the provided data is invalid (e.g., department or courses do not exist).
	 */
	StudentResponseDto addStudent(CreateStudentRequestDto requestDto);

	/**
	 * Updates an existing student's record identified by their business ID.
	 *
	 * @param studentId  The unique business ID of the student to be updated.
	 * @param requestDto The DTO containing the updated information.
	 * @return A {@link StudentResponseDto} representing the updated student.
	 * @throws com.aurionpro.studentmanagement.exception.ResourceNotFoundException if no student is found with the given ID.
	 * @throws com.aurionpro.studentmanagement.exception.ValidationException if the updated data is invalid.
	 */
	StudentResponseDto updateStudent(String studentId, UpdateStudentRequestDto requestDto);

	/**
	 * Soft-deletes a student by marking their record as inactive.
	 * The student's data is preserved in the database.
	 *
	 * @param studentId The unique business ID of the student to be deactivated.
	 * @throws com.aurionpro.studentmanagement.exception.ResourceNotFoundException if no student is found with the given ID.
	 */
	void softDeleteStudent(String studentId);

	/**
	 * Toggles the active status of a student (active to inactive, or vice versa).
	 *
	 * @param studentId The unique business ID of the student whose status will be toggled.
	 * @return A {@link StudentResponseDto} representing the student with the updated status.
	 * @throws com.aurionpro.studentmanagement.exception.ResourceNotFoundException if no student is found with the given ID.
	 */
	StudentResponseDto toggleStudentStatus(String studentId);

	/**
	 * Generates an Excel file containing a list of students based on filter criteria.
	 *
	 * @param filter   A string for searching across multiple fields. Can be null.
	 * @param isActive A boolean to filter by active status. Can be null.
	 * @param response The HttpServletResponse to which the Excel file will be written.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	void generateStudentsExcel(String filter, Boolean isActive, HttpServletResponse response) throws IOException;

	/**
	 * Generates a CSV file containing a list of students based on filter criteria.
	 *
	 * @param filter   A string for searching across multiple fields. Can be null.
	 * @param isActive A boolean to filter by active status. Can be null.
	 * @param response The HttpServletResponse to which the CSV file will be written.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	void generateStudentsCsv(String filter, Boolean isActive, HttpServletResponse response) throws IOException;
}