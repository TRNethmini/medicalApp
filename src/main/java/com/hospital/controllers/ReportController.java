package com.hospital.controllers;

import com.hospital.dao.PatientDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.AppointmentDAO;
import com.hospital.models.Patient;
import com.hospital.models.Doctor;
import com.hospital.models.Appointment;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportController {
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private AppointmentDAO appointmentDAO;

    public ReportController() {
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        this.appointmentDAO = new AppointmentDAO();
    }

    public boolean generatePatientReport(String type, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph(type.equals("all") ? "All Patients Report" : "New Patients Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Add date
            document.add(new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            document.add(new Paragraph("\n"));

            // Create table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            
            // Add headers
            addTableHeader(table, new String[]{"Name", "Age", "Gender", "Contact", "Email", "Medical History"});

            // Add data
            List<Patient> patients = type.equals("all") ? 
                patientDAO.getAllPatients() : 
                patientDAO.getNewPatients(30);

            for (Patient patient : patients) {
                table.addCell(patient.getName());
                table.addCell(String.valueOf(patient.getAge()));
                table.addCell(patient.getGender());
                table.addCell(patient.getContactNumber());
                table.addCell(patient.getEmail());
                table.addCell(patient.getMedicalHistory());
            }

            document.add(table);
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean generateDoctorReport(String type, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Doctors Directory", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            addTableHeader(table, new String[]{"Name", "Specialization", "Contact", "Email", "Availability"});

            List<Doctor> doctors = doctorDAO.getAllDoctors();
            if (type.equals("specialization")) {
                // Group by specialization
                doctors.sort((d1, d2) -> d1.getSpecialization().compareTo(d2.getSpecialization()));
            }

            String currentSpecialization = "";
            for (Doctor doctor : doctors) {
                if (type.equals("specialization") && !doctor.getSpecialization().equals(currentSpecialization)) {
                    currentSpecialization = doctor.getSpecialization();
                    PdfPCell cell = new PdfPCell(new Phrase(currentSpecialization));
                    cell.setColspan(5);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    table.addCell(cell);
                }
                
                table.addCell(doctor.getName());
                table.addCell(doctor.getSpecialization());
                table.addCell(doctor.getContactNumber());
                table.addCell(doctor.getEmail());
                table.addCell(doctor.getAvailability());
            }

            document.add(table);
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean generateAppointmentReport(String type, LocalDate startDate, LocalDate endDate, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            String reportTitle = type.equals("daily") ? "Daily Appointment Schedule" : "Appointment Summary Report";
            Paragraph title = new Paragraph(reportTitle, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            if (startDate != null && endDate != null) {
                document.add(new Paragraph("Period: " + startDate + " to " + endDate));
            }
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            
            addTableHeader(table, new String[]{"Date", "Time", "Patient", "Doctor", "Status", "Notes"});

            List<Appointment> appointments;
            if (type.equals("daily")) {
                appointments = appointmentDAO.getTodayAppointmentsList();
            } else {
                appointments = appointmentDAO.getAppointmentsByDateRange(startDate, endDate);
            }

            for (Appointment appointment : appointments) {
                Patient patient = patientDAO.getPatientById(appointment.getPatientId());
                Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
                
                table.addCell(appointment.getAppointmentDateTime().toLocalDate().toString());
                table.addCell(appointment.getAppointmentDateTime().toLocalTime().toString());
                table.addCell(patient.getName());
                table.addCell(doctor.getName());
                table.addCell(appointment.getStatus());
                table.addCell(appointment.getNotes());
            }

            document.add(table);

            // Add statistics if it's a summary report
            if (!type.equals("daily")) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Statistics:", titleFont));
                document.add(new Paragraph("Total Appointments: " + appointments.size()));
                document.add(new Paragraph("Completed: " + 
                    appointments.stream().filter(a -> a.getStatus().equals("COMPLETED")).count()));
                document.add(new Paragraph("Cancelled: " + 
                    appointments.stream().filter(a -> a.getStatus().equals("CANCELLED")).count()));
                document.add(new Paragraph("Scheduled: " + 
                    appointments.stream().filter(a -> a.getStatus().equals("SCHEDULED")).count()));
            }

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }
}