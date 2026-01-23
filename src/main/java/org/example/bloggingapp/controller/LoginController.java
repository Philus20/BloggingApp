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

public class LoginController {

    // ==================== FXML COMPONENTS ===================
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;
    @FXML private Hyperlink signupLink;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;
    
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
            
            // Setup real-time validation
            setupRealTimeValidation();
            
            // Check if user is already logged in
            if (authService.isLoggedIn()) {
                UserEntity currentUser = authService.getCurrentUser();
                System.out.println("✅ User already logged in: " + currentUser.getUserName());
                navigateToDashboard(currentUser);
            }
            
            System.out.println("✅ LoginController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize LoginController: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Disable login button during processing
        loginButton.setDisable(true);
        loginButton.setText("Signing In...");
        if (statusLabel != null) {
            statusLabel.setText("");
        }
        
        try {
            // Professional authentication service login
            AuthenticationService.AuthResult result = authService.login(email, password);
            
            if (result.isSuccess()) {
                System.out.println("✅ Login successful for: " + result.getUser().getUserName());
                
                // Show success feedback
                if (statusLabel != null) {
                    statusLabel.setText("✅ Login successful!");
                    statusLabel.setStyle("-fx-text-fill: #28a745;");
                }
                
                // Navigate to dashboard
                navigateToDashboard(result.getUser());
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", result.getMessage());
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
                "Login failed due to database issues. Please try again later.");
                
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "System Error", 
                "An unexpected error occurred. Please try again.");
                
        } finally {
            // Re-enable login button
            loginButton.setDisable(false);
            loginButton.setText("Sign In");
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        navigateToSignup();
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Email Required", 
                "Please enter your email address first, then click 'Forgot Password'.");
            emailField.requestFocus();
            return;
        }
        
        if (!RegexPatterns.matches(email, RegexPatterns.EMAIL)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", 
                "Please enter a valid email address.");
            emailField.requestFocus();
            return;
        }
        
        try {
            String resetMessage = authService.requestPasswordReset(email);
            showAlert(Alert.AlertType.INFORMATION, "Password Reset", resetMessage);
            
        } catch (AuthenticationException e) {
            System.err.println("❌ Password reset error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Reset Failed", e.getMessage());
            
        } catch (DatabaseException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                "Failed to process password reset. Please try again later.");
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
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("📱 Blogging Platform - Welcome " + user.getUserName());
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
    
    // ==================== VALIDATION & UI ENHANCEMENTS ===================
    
    /**
     * Sets up real-time validation for form fields
     */
    private void setupRealTimeValidation() {
        // Email validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                if (RegexPatterns.matches(newVal.trim(), RegexPatterns.EMAIL)) {
                    emailField.setStyle("-fx-border-color: #28a745;");
                    if (statusLabel != null) {
                        statusLabel.setText("");
                    }
                } else {
                    emailField.setStyle("-fx-border-color: #dc3545;");
                    if (statusLabel != null) {
                        statusLabel.setText("⚠️ Invalid email format");
                        statusLabel.setStyle("-fx-text-fill: #dc3545;");
                    }
                }
            } else {
                emailField.setStyle("");
                if (statusLabel != null) {
                    statusLabel.setText("");
                }
            }
        });
        
        // Password validation
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                if (newVal.length() >= 6) {
                    passwordField.setStyle("-fx-border-color: #28a745;");
                    if (statusLabel != null) {
                        statusLabel.setText("");
                    }
                } else {
                    passwordField.setStyle("-fx-border-color: #dc3545;");
                    if (statusLabel != null) {
                        statusLabel.setText("⚠️ Password must be at least 6 characters");
                        statusLabel.setStyle("-fx-text-fill: #dc3545;");
                    }
                }
            } else {
                passwordField.setStyle("");
                if (statusLabel != null) {
                    statusLabel.setText("");
                }
            }
        });
        
        // Clear validation when user starts typing after error
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && emailField.getStyle().contains("#dc3545")) {
                emailField.setStyle("");
                if (statusLabel != null) {
                    statusLabel.setText("");
                }
            }
        });
        
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && passwordField.getStyle().contains("#dc3545")) {
                passwordField.setStyle("");
                if (statusLabel != null) {
                    statusLabel.setText("");
                }
            }
        });
    }
    
    /**
     * Highlights error field based on error code
     */
    private void highlightErrorField(String errorCode) {
        // Reset all field styles
        emailField.setStyle("");
        passwordField.setStyle("");
        
        // Highlight specific field based on error
        switch (errorCode) {
            case "EMAIL_REQUIRED":
            case "INVALID_EMAIL":
                emailField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                emailField.requestFocus();
                break;
            case "PASSWORD_REQUIRED":
                passwordField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                passwordField.requestFocus();
                break;
            case "USER_NOT_FOUND":
                emailField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                emailField.requestFocus();
                if (statusLabel != null) {
                    statusLabel.setText("❌ No account found with this email");
                    statusLabel.setStyle("-fx-text-fill: #dc3545;");
                }
                break;
            case "INVALID_PASSWORD":
                passwordField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                passwordField.requestFocus();
                if (statusLabel != null) {
                    statusLabel.setText("❌ Incorrect password");
                    statusLabel.setStyle("-fx-text-fill: #dc3545;");
                }
                break;
        }
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
        } else if (type == Alert.AlertType.WARNING) {
            dialogPane.setStyle(dialogPane.getStyle() + "-fx-border-color: #ffc107;");
        }
        
        alert.showAndWait();
    }
    
    // ==================== SESSION MANAGEMENT ===================
    
    /**
     * Gets the current logged-in user
     */
    public static UserEntity getCurrentUser() {
        return AuthenticationService.getCurrentAuthenticatedUser();
    }
    
    /**
     * Logs out the current user
     */
    public static void logout() {
        AuthenticationService.forceLogout();
        System.out.println("👋 User logged out");
    }
    
    /**
     * Checks if a user is currently logged in
     */
    public static boolean isLoggedIn() {
        return AuthenticationService.isUserLoggedIn();
    }
}

