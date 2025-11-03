package com.aurionpro.studentmanagement.service;

import java.io.IOException;
import java.util.List;

import com.aurionpro.studentmanagement.entity.Student;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;

/**
 * Service interface defining the contract for exporting student data to various file formats.
 */
public interface StudentExportService {

	/**
	 * Exports a list of student entities to an Excel (XLSX) file.
	 *
	 * @param students The list of students to be exported.
	 * @param response The HttpServletResponse to which the generated Excel file will be written.
	 * @throws IOException if an error occurs while writing to the response output stream.
	 */
	void exportToExcel(List<Student> students, HttpServletResponse response) throws IOException;

	/**
	 * Exports a list of student entities to a CSV file.
	 *
	 * @param students The list of students to be exported.
	 * @param response The HttpServletResponse to which the generated CSV file will be written.
	 * @throws IOException if an error occurs while writing to the response writer.
	 */
	void exportToCsv(List<Student> students, HttpServletResponse response) throws IOException;
	
	/**
	 * Exports a list of student entities to a PDF file using JasperReports.
	 *
	 * @param students The list of students to be exported.
	 * @param response The HttpServletResponse to which the generated PDF file will be written.
	 * @throws IOException if an error occurs while writing to the response output stream.
	 * @throws JRException if an error occurs during JasperReport processing.
	 */
	void exportToPdf(List<Student> students, HttpServletResponse response) throws IOException, JRException;
}