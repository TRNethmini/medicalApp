package com.hospital.views;

import com.hospital.controllers.PatientController;
import com.hospital.models.Patient;
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

public class PatientManagementView {
    private StackPane contentArea;
    private PatientController patientController;
    private TableView<Patient> patientTable;

    public PatientManagementView(StackPane contentArea) {
        this.contentArea = contentArea;
        this.patientController = new PatientController();
        initialize();
    }

    private void initialize() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Title
        Label title = new Label("Patient Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Add Patient Form
        GridPane form = createPatientForm();

        // Patient Table
        patientTable = createPatientTable();
        
        // Search Box
        HBox searchBox = createSearchBox();

        mainContent.getChildren().addAll(title, searchBox, form, patientTable);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(mainContent);

        refreshPatientTable();
    }

    private GridPane createPatientForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        // Form fields
        TextField nameField = new TextField();
        TextField ageField = new TextField();
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female", "Other");
        TextField addressField = new TextField();
        TextField contactField = new TextField();
        TextField emailField = new TextField();
        TextArea medicalHistoryArea = new TextArea();
        medicalHistoryArea.setPrefRowCount(3);

        // Add fields to form
        int row = 0;
        form.add(new Label("Name:"), 0, row);
        form.add(nameField, 1, row++);
        form.add(new Label("Age:"), 0, row);
        form.add(ageField, 1, row++);
        form.add(new Label("Gender:"), 0, row);
        form.add(genderCombo, 1, row++);
        form.add(new Label("Address:"), 0, row);
        form.add(addressField, 1, row++);
        form.add(new Label("Contact:"), 0, row);
        form.add(contactField, 1, row++);
        form.add(new Label("Email:"), 0, row);
        form.add(emailField, 1, row++);
        form.add(new Label("Medical History:"), 0, row);
        form.add(medicalHistoryArea, 1, row++);

        Button addButton = new Button("Add Patient");
        addButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        form.add(addButton, 1, row);

        addButton.setOnAction(e -> {
            try {
                Patient patient = new Patient(
                    0,
                    nameField.getText(),
                    Integer.parseInt(ageField.getText()),
                    genderCombo.getValue(),
                    addressField.getText(),
                    contactField.getText(),
                    emailField.getText(),
                    medicalHistoryArea.getText()
                );

                if (patientController.addPatient(patient)) {
                    clearAllFields(nameField, ageField, genderCombo, addressField, 
                             contactField, emailField, medicalHistoryArea);
                    refreshPatientTable();
                    showAlert("Success", "Patient added successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to add patient!", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid age!", Alert.AlertType.ERROR);
            }
        });

        return form;
    }

    private TableView<Patient> createPatientTable() {
        TableView<Patient> table = new TableView<>();

        TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Patient, String> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getAge())));

        TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getGender()));

        TableColumn<Patient, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getContactNumber()));

        table.getColumns().addAll(nameCol, ageCol, genderCol, contactCol);

        // Add row factory for double-click editing
        table.setRowFactory(tv -> {
            TableRow<Patient> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showEditDialog(row.getItem());
                }
            });
            return row;
        });

        return table;
    }

    private HBox createSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search patients...");
        searchField.setPrefWidth(300);

        // Add listener for real-time search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPatients(newValue);
        });

        searchBox.getChildren().add(searchField);
        return searchBox;
    }

    private void clearForm(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void clearAllFields(TextField nameField, TextField ageField, 
                              ComboBox<String> genderCombo, TextField addressField,
                              TextField contactField, TextField emailField, 
                              TextArea medicalHistoryArea) {
        nameField.clear();
        ageField.clear();
        genderCombo.setValue(null);
        addressField.clear();
        contactField.clear();
        emailField.clear();
        medicalHistoryArea.clear();
    }

    private void refreshPatientTable() {
        patientTable.getItems().clear();
        patientTable.getItems().addAll(patientController.getAllPatients());
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showEditDialog(Patient patient) {
        Dialog<Patient> dialog = new Dialog<>();
        dialog.setTitle("Edit Patient");
        dialog.setHeaderText("Edit Patient Information");

        // Create the custom dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(patient.getName());
        TextField ageField = new TextField(String.valueOf(patient.getAge()));
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("MALE", "FEMALE", "OTHER");
        genderCombo.setValue(patient.getGender());
        TextField addressField = new TextField(patient.getAddress());
        TextField contactField = new TextField(patient.getContactNumber());
        TextField emailField = new TextField(patient.getEmail());
        TextArea medicalHistoryArea = new TextArea(patient.getMedicalHistory());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Age:"), 0, 1);
        grid.add(ageField, 1, 1);
        grid.add(new Label("Gender:"), 0, 2);
        grid.add(genderCombo, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(new Label("Contact:"), 0, 4);
        grid.add(contactField, 1, 4);
        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);
        grid.add(new Label("Medical History:"), 0, 6);
        grid.add(medicalHistoryArea, 1, 6);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    patient.setName(nameField.getText());
                    patient.setAge(Integer.parseInt(ageField.getText()));
                    patient.setGender(genderCombo.getValue());
                    patient.setAddress(addressField.getText());
                    patient.setContactNumber(contactField.getText());
                    patient.setEmail(emailField.getText());
                    patient.setMedicalHistory(medicalHistoryArea.getText());
                    return patient;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter a valid age!", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedPatient -> {
            if (patientController.updatePatient(updatedPatient)) {
                refreshPatientTable();
                showAlert("Success", "Patient updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to update patient!", Alert.AlertType.ERROR);
            }
        });
    }

    // Add this method to filter patients
    private void filterPatients(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            patientTable.setItems(FXCollections.observableArrayList(patientController.getAllPatients()));
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            
            List<Patient> filteredList = patientController.getAllPatients().stream()
                .filter(patient -> 
                    patient.getName().toLowerCase().contains(lowerCaseFilter) ||
                    patient.getContactNumber().toLowerCase().contains(lowerCaseFilter) ||
                    patient.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                    String.valueOf(patient.getId()).contains(lowerCaseFilter))
                .collect(Collectors.toList());
            
            patientTable.setItems(FXCollections.observableArrayList(filteredList));
        }
    }
}