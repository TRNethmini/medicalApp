package com.hospital.controllers;

import com.hospital.dao.MedicineDAO;
import com.hospital.models.Medicine;
import java.time.LocalDate;
import java.util.List;

public class PharmacyController {
    private MedicineDAO medicineDAO;

    public PharmacyController() {
        this.medicineDAO = new MedicineDAO();
    }

    public boolean addMedicine(Medicine medicine) {
        return medicineDAO.addMedicine(medicine);
    }

    public boolean updateMedicine(Medicine medicine) {
        return medicineDAO.updateMedicine(medicine);
    }

    public boolean deleteMedicine(int id) {
        return medicineDAO.deleteMedicine(id);
    }

    public Medicine getMedicineById(int id) {
        return medicineDAO.getMedicineById(id);
    }

    public List<Medicine> getAllMedicines() {
        return medicineDAO.getAllMedicines();
    }

    public List<Medicine> getLowStockMedicines() {
        return medicineDAO.getLowStockMedicines();
    }

    public List<Medicine> getExpiringMedicines(int daysThreshold) {
        return medicineDAO.getExpiringMedicines(daysThreshold);
    }
}