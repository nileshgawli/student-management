package com.aurionpro.studentmanagement.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.aurionpro.studentmanagement.entity.Course;
import com.aurionpro.studentmanagement.entity.Student;
import com.aurionpro.studentmanagement.service.StudentExportService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
  
  @Override
  public void exportToPdf(List<Student> students, HttpServletResponse response) throws IOException, JRException {
      log.info("Starting PDF export for {} students.", students.size());

      // Prepare data for JasperReports
      List<Map<String, Object>> dataSource = students.stream().map(student -> {
          Map<String, Object> map = new HashMap<>();
          map.put("studentId", student.getStudentId());
          map.put("name", student.getFirstName() + " " + student.getLastName());
          map.put("email", student.getEmail());
          map.put("department", student.getDepartment().getName().replace("_", " "));
          map.put("courses", student.getCourses().stream().map(Course::getName).collect(Collectors.joining(", ")));
          map.put("status", student.isActive() ? "Active" : "Inactive");
          return map;
      }).collect(Collectors.toList());

      // Load and compile the JRXML template
      InputStream reportStream = getClass().getResourceAsStream("/reports/student-list.jrxml");
      if (reportStream == null) {
          log.error("JRXML template not found!");
          throw new JRException("Resource not found: /reports/student-list.jrxml");
      }
      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

      JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataSource);
      
      Map<String, Object> parameters = new HashMap<>();
      InputStream logoStream = getClass().getResourceAsStream("/images/aurionpro-logo.png");
      if (logoStream == null) {
          log.error("Logo image not found in resources! The report will be generated without a logo.");
      }
      parameters.put("LOGO_IMG", logoStream);

      // Fill the report with data and parameters
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);

      // Export the report to PDF and write to the response
      JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
      log.info("PDF export completed successfully.");
  }
}