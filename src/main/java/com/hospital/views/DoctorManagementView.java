package com.hospital.views;

import com.hospital.controllers.DoctorController;
import com.hospital.models.Doctor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorManagementView {
    private StackPane contentArea;
    private DoctorController doctorController;
    private TableView<Doctor> doctorTable;

    public DoctorManagementView(StackPane contentArea) {
        this.contentArea = contentArea;
        this.doctorController = new DoctorController();
        initialize();
    }

    private void initialize() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Title
        Label title = new Label("Doctor Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Add Doctor Form
        GridPane form = createDoctorForm();

        // Doctor Table
        doctorTable = createDoctorTable();
        
        // Search Box
        HBox searchBox = createSearchBox();

        mainContent.getChildren().addAll(title, searchBox, form, doctorTable);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(mainContent);

        refreshDoctorTable();
    }

    private GridPane createDoctorForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        // Form fields
        TextField nameField = new TextField();
        TextField specializationField = new TextField();
        TextField contactField = new TextField();
        TextField emailField = new TextField();
        TextArea availabilityArea = new TextArea();
        availabilityArea.setPrefRowCount(3);

        // Add fields to form
        int row = 0;
        form.add(new Label("Name:"), 0, row);
        form.add(nameField, 1, row++);
        form.add(new Label("Specialization:"), 0, row);
        form.add(specializationField, 1, row++);
        form.add(new Label("Contact:"), 0, row);
        form.add(contactField, 1, row++);
        form.add(new Label("Email:"), 0, row);
        form.add(emailField, 1, row++);
        form.add(new Label("Availability:"), 0, row);
        form.add(availabilityArea, 1, row++);

        Button addButton = new Button("Add Doctor");
        addButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        form.add(addButton, 1, row);

        addButton.setOnAction(e -> {
            Doctor doctor = new Doctor(
                0, 0, // id and userId will be set by the database
                nameField.getText(),
                specializationField.getText(),
                contactField.getText(),
                emailField.getText(),
                availabilityArea.getText()
            );

            if (doctorController.addDoctor(doctor)) {
                clearForm(nameField, specializationField, contactField, emailField);
                availabilityArea.clear();
                refreshDoctorTable();
                showAlert("Success", "Doctor added successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to add doctor!", Alert.AlertType.ERROR);
            }
        });

        return form;
    }

    private TableView<Doctor> createDoctorTable() {
        TableView<Doctor> table = new TableView<>();

        TableColumn<Doctor, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Doctor, String> specCol = new TableColumn<>("Specialization");
        specCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSpecialization()));

        TableColumn<Doctor, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getContactNumber()));

        TableColumn<Doctor, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmail()));

        table.getColumns().addAll(nameCol, specCol, contactCol, emailCol);

        // Add context menu for edit/delete
        table.setRowFactory(tv -> {
            TableRow<Doctor> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showEditDialog(row.getItem());
                }
            });
            return row;
        });

        return table;
    }

    private void showEditDialog(Doctor doctor) {
        Dialog<Doctor> dialog = new Dialog<>();
        dialog.setTitle("Edit Doctor");
        dialog.setHeaderText("Edit Doctor Information");

        // Create the custom dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(doctor.getName());
        TextField specField = new TextField(doctor.getSpecialization());
        TextField contactField = new TextField(doctor.getContactNumber());
        TextField emailField = new TextField(doctor.getEmail());
        TextArea availabilityArea = new TextArea(doctor.getAvailability());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Specialization:"), 0, 1);
        grid.add(specField, 1, 1);
        grid.add(new Label("Contact:"), 0, 2);
        grid.add(contactField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Availability:"), 0, 4);
        grid.add(availabilityArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                doctor.setName(nameField.getText());
                doctor.setSpecialization(specField.getText());
                doctor.setContactNumber(contactField.getText());
                doctor.setEmail(emailField.getText());
                doctor.setAvailability(availabilityArea.getText());
                return doctor;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedDoctor -> {
            if (doctorController.updateDoctor(updatedDoctor)) {
                refreshDoctorTable();
                showAlert("Success", "Doctor updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to update doctor!", Alert.AlertType.ERROR);
            }
        });
    }

    private HBox createSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search doctors...");
        searchField.setPrefWidth(300);

        // Add listener for real-time search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDoctors(newValue);
        });

        searchBox.getChildren().add(searchField);
        return searchBox;
    }

    private void clearForm(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void refreshDoctorTable() {
        doctorTable.getItems().clear();
        doctorTable.getItems().addAll(doctorController.getAllDoctors());
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Add this method to filter doctors
    private void filterDoctors(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            doctorTable.setItems(FXCollections.observableArrayList(doctorController.getAllDoctors()));
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            
            List<Doctor> filteredList = doctorController.getAllDoctors().stream()
                .filter(doctor -> 
                    doctor.getName().toLowerCase().contains(lowerCaseFilter) ||
                    doctor.getSpecialization().toLowerCase().contains(lowerCaseFilter) ||
                    doctor.getContactNumber().toLowerCase().contains(lowerCaseFilter) ||
                    doctor.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                    String.valueOf(doctor.getId()).contains(lowerCaseFilter))
                .collect(Collectors.toList());
            
            doctorTable.setItems(FXCollections.observableArrayList(filteredList));
        }
    }
}