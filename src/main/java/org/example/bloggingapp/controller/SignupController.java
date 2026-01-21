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
import org.example.bloggingapp.Database.Utils.RegexPatterns;

import java.io.IOException;
import java.time.LocalDateTime;

public class SignupController {

    // ==================== FXML COMPONENTS ===================
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ProgressBar passwordStrength;
    @FXML private Hyperlink loginLink;
    
    // ==================== SERVICE LAYER ===================
    
    private ServiceFactory serviceFactory;
    private UserService userService;
    
    // ==================== INITIALIZATION ===================

    @FXML
    private void initialize() {
        try {
            // Initialize services using ServiceFactory
            this.serviceFactory = ServiceFactory.getInstance();
            this.userService = serviceFactory.getUserService();
            
            // Setup password strength listener
            setupPasswordStrengthListener();
            
            System.out.println("✅ SignupController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize SignupController: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSignup(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Input validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields");
            return;
        }
        
        // Name validation
        if (!RegexPatterns.isLengthValid(name, 2, 50)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Name", "Name must be between 2 and 50 characters");
            return;
        }
        
        // Email validation
        if (!RegexPatterns.matches(email, RegexPatterns.EMAIL)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address");
            return;
        }
        
        // Password validation
        if (!RegexPatterns.matches(password, RegexPatterns.STRONG_PASSWORD)) {
            showAlert(Alert.AlertType.ERROR, "Weak Password", "Password must be at least 8 characters with uppercase, lowercase, numbers, and special characters");
            return;
        }
        
        try {
            // Check if user already exists
            UserEntity existingUser = userService.findByEmail(email);
            if (existingUser != null) {
                showAlert(Alert.AlertType.ERROR, "Account Exists", "An account with this email already exists");
                return;
            }
            
            // Create new user entity
            UserEntity newUser = new UserEntity();
            newUser.setUserName(name);
            newUser.setEmail(email);
            newUser.setPassword(password); // In production, hash this password
            newUser.setRole("USER");
            newUser.setCreatedAt(LocalDateTime.now());
            
            // Save user to database using service layer
            UserEntity createdUser = userService.create(newUser);
            
            System.out.println("✅ Signup successful for: " + createdUser.getUserName() + " (ID: " + createdUser.getUserId() + ")");
            
            showAlert(Alert.AlertType.INFORMATION, "Signup Successful", "Account created successfully! Please login.");
            navigateToLogin();
            
        } catch (Exception e) {
            System.err.println("❌ Signup error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Signup failed. Please try again.");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        navigateToLogin();
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load login page. Please try again.");
        }
    }
    
    // ==================== UTILITY METHODS ===================
    
    /**
     * Sets up password strength listener
     */
    private void setupPasswordStrengthListener() {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            double strength = calculatePasswordStrength(newVal);
            passwordStrength.setProgress(strength);
            
            // Update color based on strength
            if (strength < 0.3) {
                passwordStrength.setStyle("-fx-accent: #dc3545;");
            } else if (strength < 0.7) {
                passwordStrength.setStyle("-fx-accent: #ffc107;");
            } else {
                passwordStrength.setStyle("-fx-accent: #28a745;");
            }
        });
    }
    
    /**
     * Calculates password strength (0.0 to 1.0)
     */
    private double calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) return 0.0;
        
        double strength = 0.0;
        
        // Length check
        if (password.length() >= 8) strength += 0.3;
        if (password.length() >= 12) strength += 0.2;
        
        // Character variety checks
        if (password.matches(".*[a-z].*")) strength += 0.2; // lowercase
        if (password.matches(".*[A-Z].*")) strength += 0.2; // uppercase
        if (password.matches(".*[0-9].*")) strength += 0.1; // numbers
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) strength += 0.1; // special chars
        
        return Math.min(strength, 1.0);
    }
    
    /**
     * Validates email format using RegexPatterns utility
     */
    private boolean isValidEmail(String email) {
        return RegexPatterns.matches(email, RegexPatterns.EMAIL);
    }
    
    /**
     * Validates password strength using RegexPatterns utility
     */
    private boolean isStrongPassword(String password) {
        return RegexPatterns.matches(password, RegexPatterns.STRONG_PASSWORD);
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
}
