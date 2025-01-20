package com.hospital.controllers;

import com.hospital.models.User;
import com.hospital.services.AuthenticationService;
import com.hospital.views.DashboardView;
import javafx.stage.Stage;

public class LoginController {
    private AuthenticationService authService;
    
    public LoginController() {
        this.authService = new AuthenticationService();
    }
    
    public boolean handleLogin(String username, String password, Stage stage) {
        User user = authService.authenticate(username, password);
        
        if (user != null) {
            // Launch dashboard
            new DashboardView(stage, user);
            return true;
        }
        return false;
    }

    public User login(String username, String password) {
        return authService.authenticate(username, password);
    }
}