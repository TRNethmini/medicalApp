package com.hospital.controllers;

import com.hospital.dao.DoctorDAO;
import com.hospital.models.Doctor;
import java.util.List;

public class DoctorController {
    private DoctorDAO doctorDAO;

    public DoctorController() {
        this.doctorDAO = new DoctorDAO();
    }

    public boolean addDoctor(Doctor doctor) {
        // Validate doctor data
        if (!validateDoctorData(doctor)) {
            return false;
        }
        return doctorDAO.addDoctor(doctor);
    }

    public boolean updateDoctor(Doctor doctor) {
        // Validate doctor data
        if (!validateDoctorData(doctor)) {
            return false;
        }
        return doctorDAO.updateDoctor(doctor);
    }

    public boolean deleteDoctor(int doctorId) {
        return doctorDAO.deleteDoctor(doctorId);
    }

    public Doctor getDoctorById(int doctorId) {
        return doctorDAO.getDoctorById(doctorId);
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.getAllDoctors();
    }

    public List<Doctor> searchDoctors(String searchTerm) {
        return doctorDAO.searchDoctors(searchTerm);
    }

    private boolean validateDoctorData(Doctor doctor) {
        // Check for null values
        if (doctor.getName() == null || doctor.getSpecialization() == null || 
            doctor.getContactNumber() == null || doctor.getEmail() == null) {
            return false;
        }

        // Check for empty strings
        if (doctor.getName().trim().isEmpty() || 
            doctor.getSpecialization().trim().isEmpty() || 
            doctor.getContactNumber().trim().isEmpty()) {
            return false;
        }

        // Validate email format
        if (!isValidEmail(doctor.getEmail())) {
            return false;
        }

        // Validate phone number format (basic validation)
        if (!isValidPhoneNumber(doctor.getContactNumber())) {
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phone) {
        // Basic phone number validation (can be enhanced based on requirements)
        String phoneRegex = "^[0-9()-+\\s]{10,}$";
        return phone.matches(phoneRegex);
    }
}