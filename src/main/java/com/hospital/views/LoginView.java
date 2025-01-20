package com.hospital.views;

import com.hospital.controllers.LoginController;
import com.hospital.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {
    private Stage stage;
    private LoginController loginController;

    public LoginView(Stage stage) {  // Constructor now accepts Stage parameter
        this.stage = stage;
        this.loginController = new LoginController();
        initialize();
    }

    private void initialize() {
        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(50));
        mainContent.setStyle("-fx-background-color: white;");

        // Title
        Label title = new Label("Hospital Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Login form
        GridPane form = createLoginForm();

        mainContent.getChildren().addAll(title, form);

        Scene scene = new Scene(mainContent, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Login - Hospital Management System");
    }

    private GridPane createLoginForm() {
        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(25));

        // Username field
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        userField.setPromptText("Enter username");

        // Password field
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

        // Error message
        Label errorMsg = new Label();
        errorMsg.setTextFill(Color.RED);

        // Add to form
        form.add(userLabel, 0, 0);
        form.add(userField, 1, 0);
        form.add(passLabel, 0, 1);
        form.add(passField, 1, 1);
        form.add(loginButton, 1, 2);
        form.add(errorMsg, 1, 3);

        // Login button action
        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorMsg.setText("Please enter both username and password");
                return;
            }

            User user = loginController.login(username, password);
            if (user != null) {
                // Show dashboard
                showDashboard(user);
            } else {
                errorMsg.setText("Invalid username or password");
            }
        });

        return form;
    }

    private void showDashboard(User user) {
        DashboardView dashboardView = new DashboardView(stage, user);
        Scene scene = new Scene(dashboardView.getRoot(), 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Dashboard - Hospital Management System");
        stage.setMaximized(true);
    }
}