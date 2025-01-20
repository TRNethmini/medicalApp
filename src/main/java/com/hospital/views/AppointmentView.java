package com.hospital.views;

import com.hospital.controllers.AppointmentController;
import com.hospital.models.Appointment;
import com.hospital.models.Doctor;
import com.hospital.models.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.util.Callback;
import java.util.function.Function;

public class AppointmentView {
    private StackPane contentArea;
    private AppointmentController appointmentController;
    private TableView<Appointment> appointmentTable;
    private SearchableComboBox<Patient> patientComboBox;
    private SearchableComboBox<Doctor> doctorComboBox;
    private DatePicker datePicker;
    private ComboBox<String> timeComboBox;
    private TextArea notesArea;

    public AppointmentView(StackPane contentArea) {
        this.contentArea = contentArea;
        this.appointmentController = new AppointmentController();
        initialize();
    }

    private void initialize() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Title
        Label title = new Label("Appointment Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Add Appointment Form
        GridPane form = createAppointmentForm();

        // Appointment Table
        appointmentTable = createAppointmentTable();
        
        mainContent.getChildren().addAll(title, form, appointmentTable);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(mainContent);

        refreshAppointmentTable();
    }

    private GridPane createAppointmentForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        int row = 0;

        // Patient ComboBox
        Label patientLabel = new Label("Patient:");
        ObservableList<Patient> patients = FXCollections.observableArrayList(appointmentController.getAllPatients());
        ComboBox<Patient> patientComboBox = new ComboBox<>(patients);
        patientComboBox.setPromptText("Select Patient");
        patientComboBox.setPrefWidth(300);
        patientComboBox.setCellFactory(lv -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.getName() + " - " + patient.getContactNumber());
                }
            }
        });
        patientComboBox.setButtonCell(new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.getName() + " - " + patient.getContactNumber());
                }
            }
        });

        form.add(patientLabel, 0, row);
        form.add(patientComboBox, 1, row++);

        // Doctor ComboBox
        Label doctorLabel = new Label("Doctor:");
        ObservableList<Doctor> doctors = FXCollections.observableArrayList(appointmentController.getAllDoctors());
        ComboBox<Doctor> doctorComboBox = new ComboBox<>(doctors);
        doctorComboBox.setPromptText("Select Doctor");
        doctorComboBox.setPrefWidth(300);
        doctorComboBox.setCellFactory(lv -> new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                if (empty || doctor == null) {
                    setText(null);
                } else {
                    setText(doctor.getName() + " - " + doctor.getSpecialization());
                }
            }
        });
        doctorComboBox.setButtonCell(new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                if (empty || doctor == null) {
                    setText(null);
                } else {
                    setText(doctor.getName() + " - " + doctor.getSpecialization());
                }
            }
        });

        form.add(doctorLabel, 0, row);
        form.add(doctorComboBox, 1, row++);

        // Date Picker
        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(300);
        form.add(dateLabel, 0, row);
        form.add(datePicker, 1, row++);

        // Time ComboBox
        Label timeLabel = new Label("Time:");
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.setItems(FXCollections.observableArrayList(
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
        ));
        timeComboBox.setPromptText("Select Time");
        timeComboBox.setPrefWidth(300);
        form.add(timeLabel, 0, row);
        form.add(timeComboBox, 1, row++);

        // Notes TextArea
        Label notesLabel = new Label("Notes:");
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        notesArea.setPrefWidth(300);
        form.add(notesLabel, 0, row);
        form.add(notesArea, 1, row++);

        // Schedule Button
        Button scheduleButton = new Button("Schedule Appointment");
        scheduleButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        scheduleButton.setPrefWidth(200);
        
        form.add(scheduleButton, 1, row);
        GridPane.setHalignment(scheduleButton, HPos.RIGHT);

        // Add button handler
        scheduleButton.setOnAction(e -> {
            if (validateForm(patientComboBox, doctorComboBox, datePicker, timeComboBox)) {
                scheduleAppointment(
                    patientComboBox.getValue(),
                    doctorComboBox.getValue(),
                    datePicker.getValue(),
                    timeComboBox.getValue(),
                    notesArea.getText()
                );
            }
        });

        return form;
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
            Patient patient = appointmentController.getPatientById(cellData.getValue().getPatientId());
            return new javafx.beans.property.SimpleStringProperty(patient.getName());
        });

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(cellData -> {
            Doctor doctor = appointmentController.getDoctorById(cellData.getValue().getDoctorId());
            return new javafx.beans.property.SimpleStringProperty(doctor.getName());
        });

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        TableColumn<Appointment, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNotes()));

        table.getColumns().addAll(dateTimeCol, patientCol, doctorCol, statusCol, notesCol);

        // Add context menu for status update
        table.setRowFactory(tv -> {
            TableRow<Appointment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showUpdateStatusDialog(row.getItem());
                }
            });
            return row;
        });

        return table;
    }

    private void showUpdateStatusDialog(Appointment appointment) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update Appointment Status");
        dialog.setHeaderText("Update status for appointment");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("SCHEDULED", "COMPLETED", "CANCELLED");
        statusCombo.setValue(appointment.getStatus());

        dialog.getDialogPane().setContent(statusCombo);
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return statusCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newStatus -> {
            appointment.setStatus(newStatus);
            if (appointmentController.updateAppointment(appointment)) {
                refreshAppointmentTable();
                showAlert("Success", "Appointment status updated!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to update appointment status!", Alert.AlertType.ERROR);
            }
        });
    }

    private void clearForm() {
        patientComboBox.setValue(null);
        doctorComboBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        timeComboBox.setValue(null);
        notesArea.clear();
    }

    private void refreshAppointmentTable() {
        appointmentTable.getItems().clear();
        appointmentTable.getItems().addAll(appointmentController.getAllAppointments());
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean validateForm(ComboBox<Patient> patientCombo, 
                               ComboBox<Doctor> doctorCombo,
                               DatePicker datePicker,
                               ComboBox<String> timeCombo) {
        StringBuilder errorMessage = new StringBuilder();

        if (patientCombo.getValue() == null) {
            errorMessage.append("Please select a patient\n");
        }
        if (doctorCombo.getValue() == null) {
            errorMessage.append("Please select a doctor\n");
        }
        if (datePicker.getValue() == null) {
            errorMessage.append("Please select a date\n");
        }
        if (timeCombo.getValue() == null || timeCombo.getValue().isEmpty()) {
            errorMessage.append("Please select a time\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void scheduleAppointment(Patient patient, Doctor doctor, LocalDate date, String time, String notes) {
        try {
            // Parse the time string to create a LocalDateTime
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                date,
                LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
            );

            // Create and save the appointment
            Appointment appointment = new Appointment(
                0, // ID will be set by database
                patient.getId(),
                doctor.getId(),
                appointmentDateTime,
                notes,
                "SCHEDULED" // Default status
            );

            if (appointmentController.scheduleAppointment(appointment)) {
                showAlert("Success", "Appointment scheduled successfully!", Alert.AlertType.INFORMATION);
                clearForm();
                refreshAppointmentTable(); // Refresh the appointments table
            } else {
                showAlert("Error", "Failed to schedule appointment.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while scheduling the appointment: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }

    private class SearchableComboBox<T> extends ComboBox<T> {
        private final ObservableList<T> originalItems;
        private final Function<T, String> converter;

        public SearchableComboBox(ObservableList<T> items, Function<T, String> converter) {
            super(items);
            this.originalItems = items;
            this.converter = converter;
            
            // Set cell factory for dropdown items
            setCellFactory(lv -> new ListCell<T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(converter.apply(item));
                    }
                }
            });

            // Set button cell for selected item display
            setButtonCell(new ListCell<T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(converter.apply(item));
                    }
                }
            });

            // Add a listener to filter items when text changes
            getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    setItems(originalItems);
                } else {
                    String searchText = newValue.toLowerCase();
                    ObservableList<T> filteredItems = FXCollections.observableArrayList();
                    for (T item : originalItems) {
                        if (converter.apply(item).toLowerCase().contains(searchText)) {
                            filteredItems.add(item);
                        }
                    }
                    setItems(filteredItems);
                }
            });

            // Set up the selection behavior
            setOnAction(e -> {
                T selectedItem = getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    getEditor().setText(converter.apply(selectedItem));
                }
            });
        }
    }
}