package com.hospital.dao;

import com.hospital.models.Medicine;
import com.hospital.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {
    
    public boolean addMedicine(Medicine medicine) {
        String sql = "INSERT INTO medicines (name, description, price, quantity, manufacturer, " +
                    "expiry_date, category, location, minimum_stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, medicine.getName());
            pstmt.setString(2, medicine.getDescription());
            pstmt.setDouble(3, medicine.getPrice());
            pstmt.setInt(4, medicine.getQuantity());
            pstmt.setString(5, medicine.getManufacturer());
            pstmt.setDate(6, Date.valueOf(medicine.getExpiryDate()));
            pstmt.setString(7, medicine.getCategory());
            pstmt.setString(8, medicine.getLocation());
            pstmt.setInt(9, medicine.getMinimumStock());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        medicine.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMedicine(Medicine medicine) {
        String sql = "UPDATE medicines SET name=?, description=?, price=?, quantity=?, " +
                    "manufacturer=?, expiry_date=?, category=?, location=?, minimum_stock=? WHERE id=?";
                    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, medicine.getName());
            pstmt.setString(2, medicine.getDescription());
            pstmt.setDouble(3, medicine.getPrice());
            pstmt.setInt(4, medicine.getQuantity());
            pstmt.setString(5, medicine.getManufacturer());
            pstmt.setDate(6, Date.valueOf(medicine.getExpiryDate()));
            pstmt.setString(7, medicine.getCategory());
            pstmt.setString(8, medicine.getLocation());
            pstmt.setInt(9, medicine.getMinimumStock());
            pstmt.setInt(10, medicine.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMedicine(int id) {
        String sql = "DELETE FROM medicines WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Medicine getMedicineById(int id) {
        String sql = "SELECT * FROM medicines WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMedicineFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                medicines.add(extractMedicineFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicines;
    }

    public List<Medicine> getLowStockMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE quantity <= minimum_stock ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                medicines.add(extractMedicineFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicines;
    }

    public List<Medicine> getExpiringMedicines(int daysThreshold) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                    "ORDER BY expiry_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, daysThreshold);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                medicines.add(extractMedicineFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicines;
    }

    private Medicine extractMedicineFromResultSet(ResultSet rs) throws SQLException {
        return new Medicine(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getInt("quantity"),
            rs.getString("manufacturer"),
            rs.getDate("expiry_date").toLocalDate(),
            rs.getString("category"),
            rs.getString("location"),
            rs.getInt("minimum_stock")
        );
    }
}