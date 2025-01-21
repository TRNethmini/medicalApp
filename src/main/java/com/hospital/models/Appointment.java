package com.hospital.models;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private int patientId;
    private int doctorId;
    private LocalDateTime appointmentDateTime;
    private String notes;
    private String status;

    public Appointment(int id, int patientId, int doctorId, LocalDateTime appointmentDateTime, 
                      String notes, String status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.notes = notes;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public String getNotes() { return notes; }
    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setStatus(String status) { this.status = status; }
}