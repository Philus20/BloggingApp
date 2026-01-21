package org.example.bloggingapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;
    @FXML private Hyperlink signupLink;
    @FXML private Hyperlink forgotPasswordLink;

    @FXML
    private void handleLogin(ActionEvent event) {
        // Simple validation
        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            System.out.println("Please enter email and password");
            return;
        }
        
        // For demo purposes, accept any login
        System.out.println("Login successful for: " + emailField.getText());
        navigateToDashboard();
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        navigateToSignup();
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        System.out.println("Forgot password clicked");
        // TODO: Implement forgot password functionality
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/fxml/main_feed.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("📱 Blogging Platform - Main Feed");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load main feed: " + e.getMessage());
        }
    }

    private void navigateToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/screens/Signup/Signup.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root, 900, 650);
            stage.setTitle("Blogging App - Sign Up");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load signup: " + e.getMessage());
        }
    }
}

