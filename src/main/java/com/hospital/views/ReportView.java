package com.hospital.views;

import com.hospital.controllers.ReportController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;

public class ReportView {
    private StackPane contentArea;
    private ReportController reportController;

    public ReportView(StackPane contentArea) {
        this.contentArea = contentArea;
        this.reportController = new ReportController();
        initialize();
    }

    private void initialize() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Title
        Label title = new Label("Reports Generation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Report Types Section
        VBox reportTypes = createReportTypesSection();

        mainContent.getChildren().addAll(title, reportTypes);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(mainContent);
    }

    private VBox createReportTypesSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");

        // Patient Reports
        TitledPane patientReports = createPatientReportsPane();
        
        // Doctor Reports
        TitledPane doctorReports = createDoctorReportsPane();
        
        // Appointment Reports
        TitledPane appointmentReports = createAppointmentReportsPane();

        section.getChildren().addAll(patientReports, doctorReports, appointmentReports);
        return section;
    }

    private TitledPane createPatientReportsPane() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Button allPatientsBtn = new Button("Generate All Patients Report");
        allPatientsBtn.setMaxWidth(Double.MAX_VALUE);
        allPatientsBtn.setOnAction(e -> generatePatientReport("all"));

        Button newPatientsBtn = new Button("Generate New Patients Report (Last 30 Days)");
        newPatientsBtn.setMaxWidth(Double.MAX_VALUE);
        newPatientsBtn.setOnAction(e -> generatePatientReport("new"));

        content.getChildren().addAll(allPatientsBtn, newPatientsBtn);
        return new TitledPane("Patient Reports", content);
    }

    private TitledPane createDoctorReportsPane() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Button allDoctorsBtn = new Button("Generate Doctors Directory");
        allDoctorsBtn.setMaxWidth(Double.MAX_VALUE);
        allDoctorsBtn.setOnAction(e -> generateDoctorReport("directory"));

        Button specializationBtn = new Button("Generate Doctors by Specialization");
        specializationBtn.setMaxWidth(Double.MAX_VALUE);
        specializationBtn.setOnAction(e -> generateDoctorReport("specialization"));

        content.getChildren().addAll(allDoctorsBtn, specializationBtn);
        return new TitledPane("Doctor Reports", content);
    }

    private TitledPane createAppointmentReportsPane() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Date range picker
        DatePicker startDate = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker endDate = new DatePicker(LocalDate.now());
        
        Button appointmentSummaryBtn = new Button("Generate Appointment Summary");
        appointmentSummaryBtn.setMaxWidth(Double.MAX_VALUE);
        appointmentSummaryBtn.setOnAction(e -> generateAppointmentReport("summary", 
            startDate.getValue(), endDate.getValue()));

        Button dailyScheduleBtn = new Button("Generate Today's Schedule");
        dailyScheduleBtn.setMaxWidth(Double.MAX_VALUE);
        dailyScheduleBtn.setOnAction(e -> generateAppointmentReport("daily", null, null));

        content.getChildren().addAll(
            new Label("Start Date:"), startDate,
            new Label("End Date:"), endDate,
            appointmentSummaryBtn, dailyScheduleBtn
        );
        
        return new TitledPane("Appointment Reports", content);
    }

    private void generatePatientReport(String type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        
        File file = fileChooser.showSaveDialog(contentArea.getScene().getWindow());
        if (file != null) {
            try {
                if (reportController.generatePatientReport(type, file.getAbsolutePath())) {
                    showAlert("Success", "Report generated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to generate report!", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                showAlert("Error", "Error generating report: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void generateDoctorReport(String type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        
        File file = fileChooser.showSaveDialog(contentArea.getScene().getWindow());
        if (file != null) {
            try {
                if (reportController.generateDoctorReport(type, file.getAbsolutePath())) {
                    showAlert("Success", "Report generated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to generate report!", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                showAlert("Error", "Error generating report: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void generateAppointmentReport(String type, LocalDate startDate, LocalDate endDate) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        
        File file = fileChooser.showSaveDialog(contentArea.getScene().getWindow());
        if (file != null) {
            try {
                if (reportController.generateAppointmentReport(type, startDate, endDate, file.getAbsolutePath())) {
                    showAlert("Success", "Report generated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to generate report!", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                showAlert("Error", "Error generating report: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}