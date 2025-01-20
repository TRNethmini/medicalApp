package com.hospital;

import com.hospital.views.LoginView;
import com.hospital.views.DashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Hospital Management System");
        
        // Show login view
        new LoginView(stage);
        
        stage.show();
    }

    @Override
    public void stop() {
        if (stage != null && stage.getScene() != null) {
            Object root = stage.getScene().getRoot();
            if (root instanceof BorderPane) {
                Object center = ((BorderPane) root).getCenter();
                if (center instanceof DashboardView) {
                    ((DashboardView) center).cleanup();
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}