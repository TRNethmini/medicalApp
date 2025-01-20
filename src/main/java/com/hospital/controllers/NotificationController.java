package com.hospital.controllers;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.models.Appointment;
import com.hospital.models.Patient;
import com.hospital.models.Doctor;
import com.hospital.services.EmailService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationController {
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private EmailService emailService;

    public NotificationController() {
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        this.emailService = new EmailService();
    }

    public List<Appointment> getUpcomingAppointments() {
        return appointmentDAO.getUpcomingAppointments();
    }

    public String getPatientName(int patientId) {
        Patient patient = patientDAO.getPatientById(patientId);
        return patient != null ? patient.getName() : "Unknown";
    }

    public String getDoctorName(int doctorId) {
        Doctor doctor = doctorDAO.getDoctorById(doctorId);
        return doctor != null ? doctor.getName() : "Unknown";
    }

    public int sendRemindersForTomorrow() {
        List<Appointment> appointments = appointmentDAO.getTomorrowAppointments();
        return sendReminders(appointments);
    }

    public int sendRemindersForDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDateRange(startDate.toLocalDate(), endDate.toLocalDate());
        return sendReminders(appointments);
    }

    public boolean sendReminderForAppointment(Appointment appointment) {
        Patient patient = patientDAO.getPatientById(appointment.getPatientId());
        Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
        
        if (patient == null || doctor == null || patient.getEmail() == null) {
            return false;
        }

        String dateTime = appointment.getAppointmentDateTime()
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a"));

        return emailService.sendAppointmentReminder(
            patient.getEmail(),
            patient.getName(),
            doctor.getName(),
            dateTime,
            appointment.getNotes()
        );
    }

    private int sendReminders(List<Appointment> appointments) {
        int sentCount = 0;
        for (Appointment appointment : appointments) {
            if (sendReminderForAppointment(appointment)) {
                sentCount++;
            }
        }
        return sentCount;
    }
}