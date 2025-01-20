package com.hospital.views;

import java.time.LocalDate;
import java.util.List;

import com.hospital.controllers.AppointmentController;
import com.hospital.controllers.DoctorController;
import com.hospital.controllers.PatientController;
import com.hospital.controllers.PharmacyController;
import com.hospital.models.Medicine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class QuickView {
    private StackPane contentArea;
    private PatientController patientController;
    private DoctorController doctorController;
    private AppointmentController appointmentController;
    private PharmacyController pharmacyController;

    public QuickView(StackPane contentArea) {
        this.contentArea = contentArea;
        this.patientController = new PatientController();
        this.doctorController = new DoctorController();
        this.appointmentController = new AppointmentController();
        this.pharmacyController = new PharmacyController();
        initialize();
    }

    private void initialize() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("Hospital Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Create tiles grid
        GridPane tilesGrid = createTilesGrid();

        mainContent.getChildren().addAll(title, tilesGrid);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(mainContent);
    }

    private GridPane createTilesGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        // Patients Tile
        VBox patientsTile = createTile(
            "Total Patients",
            String.valueOf(patientController.getAllPatients().size()),
            "New this month: " + getNewPatientsThisMonth(),
            "#2ecc71"  // Green
        );
        grid.add(patientsTile, 0, 0);

        // Doctors Tile
        VBox doctorsTile = createTile(
            "Total Doctors",
            String.valueOf(doctorController.getAllDoctors().size()),
            "Available: " + doctorController.getAllDoctors().size(),
            "#3498db"  // Blue
        );
        grid.add(doctorsTile, 1, 0);

        // Appointments Tile
        int totalAppointments = appointmentController.getAllAppointments().size();
        int todayAppointments = getTodayAppointmentsCount();
        VBox appointmentsTile = createTile(
            "Appointments",
            String.valueOf(totalAppointments),
            "Today: " + todayAppointments,
            "#e74c3c"  // Red
        );
        grid.add(appointmentsTile, 0, 1);

        // Medicines Tile
        List<Medicine> lowStockMedicines = pharmacyController.getLowStockMedicines();
        VBox medicinesTile = createTile(
            "Medicines",
            String.valueOf(pharmacyController.getAllMedicines().size()),
            "Low Stock: " + lowStockMedicines.size(),
            "#f1c40f"  // Yellow
        );
        grid.add(medicinesTile, 1, 1);

        return grid;
    }

    private VBox createTile(String title, String mainStat, String subStat, String color) {
        VBox tile = new VBox(10);
        tile.setPadding(new Insets(20));
        tile.setPrefSize(300, 200);
        tile.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-background-radius: 10;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            """, color));
        tile.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);

        Label mainStatLabel = new Label(mainStat);
        mainStatLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        mainStatLabel.setTextFill(Color.WHITE);

        Label subStatLabel = new Label(subStat);
        subStatLabel.setFont(Font.font("Arial", 14));
        subStatLabel.setTextFill(Color.WHITE);

        tile.getChildren().addAll(titleLabel, mainStatLabel, subStatLabel);
        return tile;
    }

    private int getNewPatientsThisMonth() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        return (int) patientController.getAllPatients().stream()
            .filter(patient -> patient.getRegistrationDate().isAfter(firstDayOfMonth))
            .count();
    }

    private int getTodayAppointmentsCount() {
        LocalDate today = LocalDate.now();
        return (int) appointmentController.getAllAppointments().stream()
            .filter(appointment -> appointment.getAppointmentDateTime().toLocalDate().equals(today))
            .count();
    }
} 