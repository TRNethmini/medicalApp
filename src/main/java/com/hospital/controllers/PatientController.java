package com.hospital.controllers;

import com.hospital.models.Patient;
import com.hospital.dao.PatientDAO;
import java.util.List;

public class PatientController {
    private PatientDAO patientDAO;

    public PatientController() {
        this.patientDAO = new PatientDAO();
    }

    public boolean addPatient(Patient patient) {
        if (!validatePatientData(patient)) {
            return false;
        }
        return patientDAO.addPatient(patient);
    }

    public boolean updatePatient(Patient patient) {
        if (!validatePatientData(patient)) {
            return false;
        }
        return patientDAO.updatePatient(patient);
    }

    public boolean deletePatient(int patientId) {
        return patientDAO.deletePatient(patientId);
    }

    public Patient getPatientById(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public List<Patient> searchPatients(String searchTerm) {
        return patientDAO.searchPatients(searchTerm);
    }

    private boolean validatePatientData(Patient patient) {
        if (patient.getName() == null || patient.getName().trim().isEmpty()) {
            return false;
        }
        if (patient.getAge() <= 0 || patient.getAge() > 150) {
            return false;
        }
        if (patient.getGender() == null || patient.getGender().trim().isEmpty()) {
            return false;
        }
        return true;
    }
}