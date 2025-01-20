package com.hospital.views;

import com.hospital.controllers.NotificationController;
import com.hospital.models.Appointment;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NotificationView {
    private StackPane contentArea;
    private NotificationController notificationController;
    private TableView<Appointment> appointmentTable;

    public NotificationView(StackPane contentArea) {
        this.contentArea = contentArea;
        this.notificationController = new NotificationController();
        initialize();
    }

    private void initialize() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Title
        Label title = new Label("Appointment Notifications");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Controls
        VBox controlsBox = createControlsBox();

        // Appointment Table
        appointmentTable = createAppointmentTable();
        
        // Refresh button
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshAppointmentTable());

        mainContent.getChildren().addAll(title, controlsBox, refreshButton, appointmentTable);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(mainContent);

        refreshAppointmentTable();
    }

    private VBox createControlsBox() {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");

        // Send reminders for tomorrow's appointments
        Button sendTomorrowReminders = new Button("Send Reminders for Tomorrow's Appointments");
        sendTomorrowReminders.setOnAction(e -> sendRemindersForTomorrow());

        // Send reminders for specific date range
        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();
        Button sendRangeReminders = new Button("Send Reminders for Date Range");
        sendRangeReminders.setOnAction(e -> sendRemindersForRange(startDate.getValue(), endDate.getValue()));

        // Layout
        GridPane dateGrid = new GridPane();
        dateGrid.setHgap(10);
        dateGrid.setVgap(5);
        dateGrid.add(new Label("Start Date:"), 0, 0);
        dateGrid.add(startDate, 1, 0);
        dateGrid.add(new Label("End Date:"), 0, 1);
        dateGrid.add(endDate, 1, 1);

        box.getChildren().addAll(
            sendTomorrowReminders,
            new Separator(),
            dateGrid,
            sendRangeReminders
        );

        return box;
    }

    private TableView<Appointment> createAppointmentTable() {
        TableView<Appointment> table = new TableView<>();

        TableColumn<Appointment, String> dateTimeCol = new TableColumn<>("Date & Time");
        dateTimeCol.setCellValueFactory(cellData -> {
            LocalDateTime datetime = cellData.getValue().getAppointmentDateTime();
            String formattedDateTime = datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return new javafx.beans.property.SimpleStringProperty(formattedDateTime);
        });

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(cellData -> {
            String patientName = notificationController.getPatientName(cellData.getValue().getPatientId());
            return new javafx.beans.property.SimpleStringProperty(patientName);
        });

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(cellData -> {
            String doctorName = notificationController.getDoctorName(cellData.getValue().getDoctorId());
            return new javafx.beans.property.SimpleStringProperty(doctorName);
        });

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        TableColumn<Appointment, String> notificationCol = new TableColumn<>("Notification");
        notificationCol.setCellFactory(tc -> new TableCell<>() {
            final Button button = new Button("Send Reminder");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    button.setOnAction(event -> {
                        Appointment appointment = getTableView().getItems().get(getIndex());
                        sendReminderForAppointment(appointment);
                    });
                    setGraphic(button);
                }
            }
        });

        table.getColumns().addAll(dateTimeCol, patientCol, doctorCol, statusCol, notificationCol);
        return table;
    }

    private void sendRemindersForTomorrow() {
        int sent = notificationController.sendRemindersForTomorrow();
        showAlert("Reminders Sent", 
            String.format("Successfully sent %d reminder(s) for tomorrow's appointments.", sent),
            Alert.AlertType.INFORMATION);
    }

    private void sendRemindersForRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            showAlert("Error", "Please select both start and end dates.", Alert.AlertType.ERROR);
            return;
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        int sent = notificationController.sendRemindersForDateRange(startDateTime, endDateTime);
        showAlert("Reminders Sent", 
            String.format("Successfully sent %d reminder(s) for the selected date range.", sent),
            Alert.AlertType.INFORMATION);
    }

    private void sendReminderForAppointment(Appointment appointment) {
        if (notificationController.sendReminderForAppointment(appointment)) {
            showAlert("Success", "Reminder sent successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to send reminder.", Alert.AlertType.ERROR);
        }
    }

    private void refreshAppointmentTable() {
        appointmentTable.getItems().clear();
        appointmentTable.getItems().addAll(notificationController.getUpcomingAppointments());
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}