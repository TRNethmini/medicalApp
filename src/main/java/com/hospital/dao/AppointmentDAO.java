package com.hospital.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.hospital.models.Appointment;
import com.hospital.utils.DatabaseConnection;

public class AppointmentDAO {
    
    public boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_datetime, status, notes) " +
                    "VALUES (?, ?, ?, ?, ?)";
                    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            pstmt.setString(4, appointment.getStatus());
            pstmt.setString(5, appointment.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        appointment.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, " +
                    "appointment_datetime = ?, status = ?, notes = ? WHERE id = ?";
                    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            pstmt.setString(4, appointment.getStatus());
            pstmt.setString(5, appointment.getNotes());
            pstmt.setInt(6, appointment.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_datetime";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt("id"),
            rs.getInt("patient_id"),
            rs.getInt("doctor_id"),
            rs.getTimestamp("appointment_datetime").toLocalDateTime(),
            rs.getString("status"),
            rs.getString("notes")
        );
    }

    public int getTodayAppointments() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE DATE(appointment_datetime) = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPendingAppointments() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status = 'SCHEDULED'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Appointment> getTodayAppointmentsList() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE DATE(appointment_datetime) = CURDATE() ORDER BY appointment_datetime";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE DATE(appointment_datetime) BETWEEN ? AND ? ORDER BY appointment_datetime";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(startDate));
            pstmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public List<Appointment> getUpcomingAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_datetime >= NOW() " +
                    "AND status = 'SCHEDULED' ORDER BY appointment_datetime";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public List<Appointment> getTomorrowAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE DATE(appointment_datetime) = DATE_ADD(CURDATE(), INTERVAL 1 DAY) " +
                    "AND status = 'SCHEDULED' ORDER BY appointment_datetime";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
}