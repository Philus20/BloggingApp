package org.example.bloggingapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignupController {

    @FXML
    private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ProgressBar passwordStrength;
    @FXML private Hyperlink loginLink;

    @FXML
    private void handleSignup(ActionEvent event) {
        // Simple validation
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            System.out.println("Please fill all fields");
            return;
        }
        
        // For demo purposes, accept any signup
        System.out.println("Signup successful for: " + nameField.getText());
        navigateToDashboard();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        navigateToLogin();
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) nameField.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Blogging App - Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load dashboard: " + e.getMessage());
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/screens/LoginResources/LoginPage.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) nameField.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Blogging App - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load login: " + e.getMessage());
        }
    }
}
