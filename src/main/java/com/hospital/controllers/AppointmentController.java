package com.hospital.controllers;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.models.Appointment;
import com.hospital.models.Patient;
import com.hospital.models.Doctor;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentController {
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;

    public AppointmentController() {
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
    }

    public boolean scheduleAppointment(Appointment appointment) {
        // Validate appointment data
        if (!validateAppointment(appointment)) {
            return false;
        }
        return appointmentDAO.addAppointment(appointment);
    }

    public boolean updateAppointment(Appointment appointment) {
        if (!validateAppointment(appointment)) {
            return false;
        }
        return appointmentDAO.updateAppointment(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.getAllDoctors();
    }

    private boolean validateAppointment(Appointment appointment) {
        // Check if patient exists
        if (patientDAO.getPatientById(appointment.getPatientId()) == null) {
            return false;
        }

        // Check if doctor exists
        if (doctorDAO.getDoctorById(appointment.getDoctorId()) == null) {
            return false;
        }

        // Check if appointment time is in the future
        if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Check if status is valid
        String status = appointment.getStatus().toUpperCase();
        if (!status.equals("SCHEDULED") && !status.equals("COMPLETED") && !status.equals("CANCELLED")) {
            return false;
        }

        return true;
    }

    public Patient getPatientById(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    public Doctor getDoctorById(int doctorId) {
        return doctorDAO.getDoctorById(doctorId);
    }
}