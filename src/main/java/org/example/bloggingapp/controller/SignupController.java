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
import org.example.bloggingapp.Services.UserService;
import org.example.bloggingapp.Services.AuthenticationService;
import org.example.bloggingapp.Utils.RegexPatterns;
import org.example.bloggingapp.Utils.Exceptions.AuthenticationException;
import org.example.bloggingapp.Utils.Exceptions.ValidationException;
import org.example.bloggingapp.Utils.Exceptions.DatabaseException;

import java.io.IOException;

public class SignupController {

    // ==================== FXML COMPONENTS ===================
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ProgressBar passwordStrength;
    @FXML private Label passwordStrengthLabel;
    @FXML private Hyperlink loginLink;
    @FXML private Button signupButton;
    
    // ==================== SERVICE LAYER ===================
    
    private ServiceFactory serviceFactory;
    private UserService userService;
    private AuthenticationService authService;
    
    // ==================== INITIALIZATION ===================

    @FXML
    private void initialize() {
        try {
            // Initialize services using ServiceFactory
            this.serviceFactory = ServiceFactory.getInstance();
            this.userService = serviceFactory.getUserService();
            this.authService = new AuthenticationService(userService);
            
            // Setup password strength listener
            setupPasswordStrengthListener();
            
            // Setup real-time validation
            setupRealTimeValidation();
            
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
        String confirmPassword = confirmPasswordField.getText();
        
        // Disable signup button during processing
        signupButton.setDisable(true);
        signupButton.setText("Creating Account...");
        
        try {
            // Professional authentication service signup
            AuthenticationService.AuthResult result = authService.signup(name, email, password, confirmPassword);
            
            if (result.isSuccess()) {
                System.out.println("✅ Signup successful for: " + result.getUser().getUserName());
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "🎉 Welcome!", 
                    "Account created successfully!\n\nWelcome to Blogging App, " + 
                    result.getUser().getUserName() + "!\n\nYou are now logged in.");
                
                // Navigate to dashboard
                navigateToDashboard(result.getUser());
            } else {
                showAlert(Alert.AlertType.ERROR, "Signup Failed", result.getMessage());
            }
            
        } catch (ValidationException e) {
            System.err.println("❌ Validation error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
            highlightErrorField(e.getErrorCode());
            
        } catch (AuthenticationException e) {
            System.err.println("❌ Authentication error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Authentication Error", e.getMessage());
            highlightErrorField(e.getErrorCode());
            
        } catch (DatabaseException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                "Unable to create account due to database issues. Please try again later.");
                
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "System Error", 
                "An unexpected error occurred. Please try again.");
                
        } finally {
            // Re-enable signup button
            signupButton.setDisable(false);
            signupButton.setText("Sign Up");
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
    
    private void navigateToDashboard(UserEntity user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/Screens/fxml/main_feed.fxml"));
            Parent root = loader.load();
            
            // Pass current user to MainFeedController
            if (loader.getController() instanceof org.example.bloggingapp.controller.MainFeedController) {
                org.example.bloggingapp.controller.MainFeedController mainFeedController = loader.getController();
                mainFeedController.setCurrentUserId(user.getUserId());
            }
            
            Stage stage = (Stage) nameField.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("📱 Blogging Platform - Welcome " + user.getUserName());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load dashboard: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load dashboard. Please try again.");
        }
    }
    
    // ==================== VALIDATION & UI ENHANCEMENTS ===================
    
    /**
     * Sets up real-time validation for form fields
     */
    private void setupRealTimeValidation() {
        // Name validation
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                if (RegexPatterns.isLengthValid(newVal.trim(), 2, 50)) {
                    nameField.setStyle("-fx-border-color: #28a745;");
                } else {
                    nameField.setStyle("-fx-border-color: #dc3545;");
                }
            } else {
                nameField.setStyle("");
            }
        });
        
        // Email validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                if (RegexPatterns.matches(newVal.trim(), RegexPatterns.EMAIL)) {
                    emailField.setStyle("-fx-border-color: #28a745;");
                } else {
                    emailField.setStyle("-fx-border-color: #dc3545;");
                }
            } else {
                emailField.setStyle("");
            }
        });
        
        // Password confirmation matching
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !passwordField.getText().isEmpty()) {
                if (newVal.equals(passwordField.getText())) {
                    confirmPasswordField.setStyle("-fx-border-color: #28a745;");
                } else {
                    confirmPasswordField.setStyle("-fx-border-color: #dc3545;");
                }
            } else {
                confirmPasswordField.setStyle("");
            }
        });
    }
    
    /**
     * Sets up password strength listener with visual feedback
     */
    private void setupPasswordStrengthListener() {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            double strength = calculatePasswordStrength(newVal);
            passwordStrength.setProgress(strength);
            
            // Update color and label based on strength
            if (strength < 0.3) {
                passwordStrength.setStyle("-fx-accent: #dc3545;");
                if (passwordStrengthLabel != null) {
                    passwordStrengthLabel.setText("Weak");
                    passwordStrengthLabel.setStyle("-fx-text-fill: #dc3545;");
                }
            } else if (strength < 0.7) {
                passwordStrength.setStyle("-fx-accent: #ffc107;");
                if (passwordStrengthLabel != null) {
                    passwordStrengthLabel.setText("Medium");
                    passwordStrengthLabel.setStyle("-fx-text-fill: #ffc107;");
                }
            } else {
                passwordStrength.setStyle("-fx-accent: #28a745;");
                if (passwordStrengthLabel != null) {
                    passwordStrengthLabel.setText("Strong");
                    passwordStrengthLabel.setStyle("-fx-text-fill: #28a745;");
                }
            }
            
            if (newVal.isEmpty()) {
                if (passwordStrengthLabel != null) {
                    passwordStrengthLabel.setText("");
                }
                passwordStrength.setProgress(0);
            }
        });
    }
    
    /**
     * Highlights error field based on error code
     */
    private void highlightErrorField(String errorCode) {
        // Reset all field styles
        nameField.setStyle("");
        emailField.setStyle("");
        passwordField.setStyle("");
        confirmPasswordField.setStyle("");
        
        // Highlight specific field based on error
        switch (errorCode) {
            case "NAME_REQUIRED":
            case "INVALID_NAME":
                nameField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                nameField.requestFocus();
                break;
            case "EMAIL_REQUIRED":
            case "INVALID_EMAIL":
                emailField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                emailField.requestFocus();
                break;
            case "PASSWORD_REQUIRED":
            case "WEAK_PASSWORD":
                passwordField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                passwordField.requestFocus();
                break;
            case "PASSWORD_MISMATCH":
                confirmPasswordField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                confirmPasswordField.requestFocus();
                break;
            case "EMAIL_EXISTS":
                emailField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                emailField.requestFocus();
                break;
        }
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
     * Shows alert dialog with enhanced styling
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 14px;");
        
        if (type == Alert.AlertType.ERROR) {
            dialogPane.setStyle(dialogPane.getStyle() + "-fx-border-color: #dc3545;");
        } else if (type == Alert.AlertType.INFORMATION) {
            dialogPane.setStyle(dialogPane.getStyle() + "-fx-border-color: #17a2b8;");
        }
        
        alert.showAndWait();
    }
}
