package org.example.bloggingapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Database.factories.ServiceFactory;
import org.example.bloggingapp.Database.Services.UserService;

import java.io.IOException;

public class LoginController {

    // ==================== FXML COMPONENTS ===================
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;
    @FXML private Hyperlink signupLink;
    @FXML private Hyperlink forgotPasswordLink;
    
    // ==================== SERVICE LAYER ===================
    
    private ServiceFactory serviceFactory;
    private UserService userService;
    
    // ==================== USER SESSION ===================
    
    private static UserEntity currentUser;
    
    // ==================== INITIALIZATION ===================

    @FXML
    private void initialize() {
        try {
            // Initialize services using ServiceFactory
            this.serviceFactory = ServiceFactory.getInstance();
            this.userService = serviceFactory.getUserService();
            
            System.out.println("✅ LoginController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize LoginController: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Input validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter email and password");
            return;
        }
        
        // Email format validation
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address");
            return;
        }
        
        try {
            // Authenticate user using service layer
            UserEntity user = userService.findByEmail(email);
            
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "No account found with this email");
                return;
            }
            
            // Password verification (in real app, use proper hashing)
            if (!verifyPassword(password, user.getPassword())) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect password");
                return;
            }
            
            // Set current user session
            currentUser = user;
            
            System.out.println("✅ Login successful for: " + user.getUserName() + " (ID: " + user.getUserId() + ")");
            navigateToDashboard();
            
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Login failed. Please try again.");
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        navigateToSignup();
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Forgot Password", "Please contact support to reset your password.");
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/fxml/main_feed.fxml"));
            Parent root = loader.load();
            
            // Pass current user to MainFeedController
            MainFeedController mainFeedController = loader.getController();
            if (mainFeedController != null) {
                mainFeedController.setCurrentUserId(currentUser.getUserId());
            }
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("📱 Blogging Platform - Main Feed");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load main feed: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load main feed. Please try again.");
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load signup page. Please try again.");
        }
    }
    
    // ==================== UTILITY METHODS ===================
    
    /**
     * Validates email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Verifies password (in real app, use proper hashing like BCrypt)
     */
    private boolean verifyPassword(String inputPassword, String storedPassword) {
        // For demo purposes, simple string comparison
        // In production, use proper password hashing
        return inputPassword.equals(storedPassword);
    }
    
    /**
     * Shows alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ==================== SESSION MANAGEMENT ===================
    
    /**
     * Gets the current logged-in user
     */
    public static UserEntity getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Logs out the current user
     */
    public static void logout() {
        currentUser = null;
        System.out.println("👋 User logged out");
    }
    
    /**
     * Checks if a user is currently logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}

