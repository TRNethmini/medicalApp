package com.hospital.views;

import com.hospital.controllers.PharmacyController;
import com.hospital.models.Medicine;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.util.Optional;

public class PharmacyView {
    private StackPane contentArea;
    private PharmacyController pharmacyController;
    private TableView<Medicine> medicineTable;

    public PharmacyView(StackPane contentArea) {
        this.contentArea = contentArea;
        this.pharmacyController = new PharmacyController();
        initialize();
    }

    private void initialize() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Title
        Label title = new Label("Pharmacy Inventory Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Action Buttons
        HBox actionButtons = createActionButtons();

        // Alert Boxes
        HBox alertBoxes = createAlertBoxes();

        // Medicine Table
        medicineTable = createMedicineTable();
        
        // Add components to main content
        mainContent.getChildren().addAll(title, actionButtons, alertBoxes, medicineTable);

        // Set the content
        contentArea.getChildren().clear();
        contentArea.getChildren().add(mainContent);

        // Initial data load
        refreshTable();
    }

    private HBox createActionButtons() {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Button addButton = new Button("Add New Medicine");
        addButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        addButton.setOnAction(e -> showAddEditDialog(null));

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshTable());

        hbox.getChildren().addAll(addButton, refreshButton);
        return hbox;
    }

    private HBox createAlertBoxes() {
        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.CENTER_LEFT);

        // Low Stock Alert
        VBox lowStockBox = createAlertBox("Low Stock Alert", 
            pharmacyController.getLowStockMedicines().size() + " items below minimum stock",
            "#e74c3c");

        // Expiring Soon Alert
        VBox expiringBox = createAlertBox("Expiring Soon", 
            pharmacyController.getExpiringMedicines(30).size() + " items expiring in 30 days",
            "#f1c40f");

        hbox.getChildren().addAll(lowStockBox, expiringBox);
        return hbox;
    }

    private VBox createAlertBox(String title, String content, String color) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");
        box.setPrefWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label contentLabel = new Label(content);
        contentLabel.setTextFill(Color.WHITE);

        box.getChildren().addAll(titleLabel, contentLabel);
        return box;
    }

    private TableView<Medicine> createMedicineTable() {
        TableView<Medicine> table = new TableView<>();

        // Create columns
        TableColumn<Medicine, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Medicine, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<Medicine, Number> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()));

        TableColumn<Medicine, Number> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()));

        TableColumn<Medicine, String> manufacturerCol = new TableColumn<>("Manufacturer");
        manufacturerCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getManufacturer()));

        TableColumn<Medicine, String> expiryCol = new TableColumn<>("Expiry Date");
        expiryCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getExpiryDate().toString()));

        // Add action column
        TableColumn<Medicine, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(e -> {
                    Medicine medicine = getTableView().getItems().get(getIndex());
                    showAddEditDialog(medicine);
                });

                deleteButton.setOnAction(e -> {
                    Medicine medicine = getTableView().getItems().get(getIndex());
                    handleDelete(medicine);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        table.getColumns().addAll(nameCol, descCol, priceCol, quantityCol, 
                                manufacturerCol, expiryCol, actionCol);

        return table;
    }

    private void showAddEditDialog(Medicine medicine) {
        Dialog<Medicine> dialog = new Dialog<>();
        dialog.setTitle(medicine == null ? "Add New Medicine" : "Edit Medicine");
        dialog.setHeaderText(null);

        // Create the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextArea descField = new TextArea();
        TextField priceField = new TextField();
        TextField quantityField = new TextField();
        TextField manufacturerField = new TextField();
        DatePicker expiryDatePicker = new DatePicker();
        TextField categoryField = new TextField();
        TextField locationField = new TextField();
        TextField minStockField = new TextField();

        // Populate fields if editing
        if (medicine != null) {
            nameField.setText(medicine.getName());
            descField.setText(medicine.getDescription());
            priceField.setText(String.valueOf(medicine.getPrice()));
            quantityField.setText(String.valueOf(medicine.getQuantity()));
            manufacturerField.setText(medicine.getManufacturer());
            expiryDatePicker.setValue(medicine.getExpiryDate());
            categoryField.setText(medicine.getCategory());
            locationField.setText(medicine.getLocation());
            minStockField.setText(String.valueOf(medicine.getMinimumStock()));
        }

        // Add fields to grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Manufacturer:"), 0, 4);
        grid.add(manufacturerField, 1, 4);
        grid.add(new Label("Expiry Date:"), 0, 5);
        grid.add(expiryDatePicker, 1, 5);
        grid.add(new Label("Category:"), 0, 6);
        grid.add(categoryField, 1, 6);
        grid.add(new Label("Location:"), 0, 7);
        grid.add(locationField, 1, 7);
        grid.add(new Label("Minimum Stock:"), 0, 8);
        grid.add(minStockField, 1, 8);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new Medicine(
                        medicine != null ? medicine.getId() : 0,
                        nameField.getText(),
                        descField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(quantityField.getText()),
                        manufacturerField.getText(),
                        expiryDatePicker.getValue(),
                        categoryField.getText(),
                        locationField.getText(),
                        Integer.parseInt(minStockField.getText())
                    );
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter valid numbers for price, quantity, and minimum stock.", 
                            Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Medicine> result = dialog.showAndWait();
        result.ifPresent(newMedicine -> {
            if (medicine == null) {
                if (pharmacyController.addMedicine(newMedicine)) {
                    showAlert("Success", "Medicine added successfully!", Alert.AlertType.INFORMATION);
                    refreshTable();
                } else {
                    showAlert("Error", "Failed to add medicine!", Alert.AlertType.ERROR);
                }
            } else {
                if (pharmacyController.updateMedicine(newMedicine)) {
                    showAlert("Success", "Medicine updated successfully!", Alert.AlertType.INFORMATION);
                    refreshTable();
                } else {
                    showAlert("Error", "Failed to update medicine!", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void handleDelete(Medicine medicine) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Medicine");
        alert.setHeaderText("Delete " + medicine.getName());
        alert.setContentText("Are you sure you want to delete this medicine?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (pharmacyController.deleteMedicine(medicine.getId())) {
                showAlert("Success", "Medicine deleted successfully!", Alert.AlertType.INFORMATION);
                refreshTable();
            } else {
                showAlert("Error", "Failed to delete medicine!", Alert.AlertType.ERROR);
            }
        }
    }

    private void refreshTable() {
        medicineTable.getItems().clear();
        medicineTable.getItems().addAll(pharmacyController.getAllMedicines());
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}