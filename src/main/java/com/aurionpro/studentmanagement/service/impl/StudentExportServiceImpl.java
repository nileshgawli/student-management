package com.aurionpro.studentmanagement.service.impl;

import com.aurionpro.studentmanagement.entity.Course;
import com.aurionpro.studentmanagement.entity.Student;
import com.aurionpro.studentmanagement.service.StudentExportService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link StudentExportService}.
 * This service handles the logic for generating Excel and CSV files from a list of students.
 * It uses Apache POI for Excel generation and a PrintWriter for CSV generation.
 */
@Service
@Slf4j
public class StudentExportServiceImpl implements StudentExportService {

  /**
   * {@inheritDoc}
   * This implementation uses the Apache POI library to create an XLSX workbook,
   * populate it with student data, and write it to the HttpServletResponse output stream.
   */
  @Override
  public void exportToExcel(List<Student> students, HttpServletResponse response) throws IOException {
    log.info("Starting Excel export for {} students.", students.size());
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet("Students");

      // Create and style the header row
      Row headerRow = sheet.createRow(0);
      CellStyle headerStyle = workbook.createCellStyle();
      XSSFFont font = workbook.createFont();
      font.setBold(true);
      headerStyle.setFont(font);

      String[] headers = { "Student ID", "First Name", "Last Name", "Email", "Department", "Courses", "Status" };
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      // Populate data rows
      int rowNum = 1;
      for (Student student : students) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(student.getStudentId());
        row.createCell(1).setCellValue(student.getFirstName());
        row.createCell(2).setCellValue(student.getLastName());
        row.createCell(3).setCellValue(student.getEmail());
        row.createCell(4).setCellValue(student.getDepartment().getName().replace("_", " "));
        String courses = student.getCourses().stream().map(Course::getName).collect(Collectors.joining(", "));
        row.createCell(5).setCellValue(courses);
        row.createCell(6).setCellValue(student.isActive() ? "Active" : "Inactive");
      }

      // Auto-size columns for better readability
      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(response.getOutputStream());
      log.info("Excel export completed successfully.");
    }
  }

  /**
   * {@inheritDoc}
   * This implementation uses a {@link PrintWriter} to write the student data in CSV format
   * directly to the HttpServletResponse writer. It formats the data, including a header row.
   */
  @Override
  public void exportToCsv(List<Student> students, HttpServletResponse response) throws IOException {
    log.info("Starting CSV export for {} students.", students.size());
    try (PrintWriter writer = response.getWriter()) {
      // Write header
      writer.println("Student ID,First Name,Last Name,Email,Department,Courses,Status");

      // Write data rows
      for (Student student : students) {
        String courses = student.getCourses().stream()
                .map(Course::getName)
                .collect(Collectors.joining("; ")); // Use semicolon in case course names have commas
        
        String[] data = {
          student.getStudentId(),
          student.getFirstName(),
          student.getLastName(),
          student.getEmail(),
          student.getDepartment().getName().replace("_", " "),
          "\"" + courses + "\"", // Quote the courses field to handle internal commas
          student.isActive() ? "Active" : "Inactive",
        };
        writer.println(String.join(",", data));
      }
      log.info("CSV export completed successfully.");
    }
  }
}