package com.hospital.views;

import com.hospital.models.User;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.AppointmentDAO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardView {
    private BorderPane root;
    private StackPane contentArea;
    private User currentUser;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private AppointmentDAO appointmentDAO;
    private VBox dashboardTiles;
    private Timeline updateTimeline;
    
    public DashboardView(Stage stage, User user) {
        this.currentUser = user;
        this.root = new BorderPane();
        this.contentArea = new StackPane();
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        this.appointmentDAO = new AppointmentDAO();
        initialize(stage);
        setupAutoUpdate();
    }
    
    private void setupAutoUpdate() {
        // Update dashboard every 30 seconds
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> updateDashboardData()));
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
    }
    
    private void updateDashboardData() {
        if (dashboardTiles != null && dashboardTiles.getChildren().size() >= 4) {
            // Update total patients
            Label patientCount = (Label) ((VBox) dashboardTiles.getChildren().get(0)).getChildren().get(1);
            patientCount.setText(String.valueOf(patientDAO.getTotalPatients()));
            
            // Update total doctors
            Label doctorCount = (Label) ((VBox) dashboardTiles.getChildren().get(1)).getChildren().get(1);
            doctorCount.setText(String.valueOf(doctorDAO.getTotalDoctors()));
            
            // Update today's appointments
            Label appointmentCount = (Label) ((VBox) dashboardTiles.getChildren().get(2)).getChildren().get(1);
            appointmentCount.setText(String.valueOf(appointmentDAO.getTodayAppointments()));
            
            // Update pending appointments
            Label pendingCount = (Label) ((VBox) dashboardTiles.getChildren().get(3)).getChildren().get(1);
            pendingCount.setText(String.valueOf(appointmentDAO.getPendingAppointments()));
        }
    }
    
    private void initialize(Stage stage) {
        // Set up the main layout
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f4f4f4;");

        // Create and set the sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Set up the content area
        contentArea.setStyle("-fx-background-color: white;");
        root.setCenter(contentArea);

        // Show QuickView instead of welcome message
        showQuickView();

        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("Hospital Management System - Dashboard");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(200);

        // Create navigation buttons
        Button patientsBtn = new Button("Patients");
        Button doctorsBtn = new Button("Doctors");
        Button appointmentsBtn = new Button("Appointments");
        Button reportsBtn = new Button("Reports");
        Button pharmacyBtn = new Button("Pharmacy");
        Button notificationsBtn = new Button("Notifications");

        // Create logout button
        Button logoutBtn = new Button("Logout");

        // Style the regular buttons
        String buttonStyle = """
            -fx-text-fill: white;
            -fx-background-color: transparent;
            -fx-alignment: CENTER-LEFT;
            -fx-min-width: 180;
            -fx-cursor: hand;
            """;
            
        patientsBtn.setStyle(buttonStyle);
        doctorsBtn.setStyle(buttonStyle);
        appointmentsBtn.setStyle(buttonStyle);
        reportsBtn.setStyle(buttonStyle);
        pharmacyBtn.setStyle(buttonStyle);
        notificationsBtn.setStyle(buttonStyle);

        // Add hover effect for navigation buttons
        String hoverStyle = buttonStyle + "-fx-background-color: #34495e;";
        
        patientsBtn.setOnMouseEntered(e -> patientsBtn.setStyle(hoverStyle));
        patientsBtn.setOnMouseExited(e -> patientsBtn.setStyle(buttonStyle));
        
        doctorsBtn.setOnMouseEntered(e -> doctorsBtn.setStyle(hoverStyle));
        doctorsBtn.setOnMouseExited(e -> doctorsBtn.setStyle(buttonStyle));
        
        appointmentsBtn.setOnMouseEntered(e -> appointmentsBtn.setStyle(hoverStyle));
        appointmentsBtn.setOnMouseExited(e -> appointmentsBtn.setStyle(buttonStyle));
        
        reportsBtn.setOnMouseEntered(e -> reportsBtn.setStyle(hoverStyle));
        reportsBtn.setOnMouseExited(e -> reportsBtn.setStyle(buttonStyle));
        
        pharmacyBtn.setOnMouseEntered(e -> pharmacyBtn.setStyle(hoverStyle));
        pharmacyBtn.setOnMouseExited(e -> pharmacyBtn.setStyle(buttonStyle));
        
        notificationsBtn.setOnMouseEntered(e -> notificationsBtn.setStyle(hoverStyle));
        notificationsBtn.setOnMouseExited(e -> notificationsBtn.setStyle(buttonStyle));

        // Special style for logout button with cursor pointer
        String logoutStyle = """
            -fx-text-fill: white;
            -fx-background-color: #e74c3c;
            -fx-alignment: CENTER;
            -fx-min-width: 180;
            -fx-padding: 10;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """;
        String logoutHoverStyle = logoutStyle + "-fx-background-color: #c0392b;";
        
        logoutBtn.setStyle(logoutStyle);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutHoverStyle));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(logoutStyle));

        // Add button actions
        patientsBtn.setOnAction(e -> showPatientManagement());
        doctorsBtn.setOnAction(e -> showDoctorManagement());
        appointmentsBtn.setOnAction(e -> showAppointments());
        reportsBtn.setOnAction(e -> showReports());
        pharmacyBtn.setOnAction(e -> showPharmacy());
        notificationsBtn.setOnAction(e -> showNotifications());
        logoutBtn.setOnAction(e -> logout());

        // Create a VBox for main navigation buttons
        VBox navButtons = new VBox(10);
        navButtons.getChildren().addAll(
            patientsBtn,
            doctorsBtn,
            appointmentsBtn,
            reportsBtn,
            pharmacyBtn,
            notificationsBtn
        );

        // Create a spacer that will push the logout button to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Add all components to sidebar
        sidebar.getChildren().addAll(navButtons, spacer, logoutBtn);

        return sidebar;
    }
    
    private void showQuickView() {
        new QuickView(contentArea);
    }
    
    private void showPatientManagement() {
        new PatientManagementView(contentArea);
    }
    
    private void showDoctorManagement() {
        new DoctorManagementView(contentArea);
    }
    
    private void showAppointments() {
        new AppointmentView(contentArea);
    }
    
    private void showReports() {
        new ReportView(contentArea);
    }
    
    private void showPharmacy() {
        new PharmacyView(contentArea);
    }
    
    private void showNotifications() {
        new NotificationView(contentArea);
    }
    
    private void logout() {
        Stage stage = (Stage) root.getScene().getWindow();
        new LoginView(stage);
    }
    
    public void cleanup() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
    }
    
    public BorderPane getRoot() {
        return root;
    }
}