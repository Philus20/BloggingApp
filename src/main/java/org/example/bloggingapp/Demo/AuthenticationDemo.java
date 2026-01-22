package org.example.bloggingapp.Demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Database.factories.ServiceFactory;
import org.example.bloggingapp.Services.UserService;
import org.example.bloggingapp.Services.AuthenticationService;
import org.example.bloggingapp.Exceptions.AuthenticationException;
import org.example.bloggingapp.Exceptions.ValidationException;
import org.example.bloggingapp.Exceptions.DatabaseException;

/**
 * Professional Authentication Demo Application
 * Demonstrates secure signup and login functionality with real-time validation
 */
public class AuthenticationDemo extends Application {
    
    private UserService userService;
    private AuthenticationService authService;
    private Stage primaryStage;
    
    // UI Components
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private ProgressBar passwordStrength;
    private Label passwordStrengthLabel;
    private Label statusLabel;
    private Button primaryButton;
    private Hyperlink secondaryLink;
    
    // Current mode
    private boolean isSignupMode = true;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        try {
            // Initialize services
            ServiceFactory serviceFactory = ServiceFactory.getInstance();
            this.userService = serviceFactory.getUserService();
            this.authService = new AuthenticationService(userService);
            
            // Check if user is already logged in
            if (authService.isLoggedIn()) {
                showDashboard(authService.getCurrentUser());
                return;
            }
            
            // Show signup form by default
            showSignupForm();
            
        } catch (Exception e) {
            showError("Initialization Error", "Failed to initialize application: " + e.getMessage());
        }
    }
    
    private void showSignupForm() {
        isSignupMode = true;
        
        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #f8f9fa;");
        
        // Title
        Text title = new Text("Create Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-fill: #2c3e50;");
        
        // Subtitle
        Text subtitle = new Text("Join our blogging community today");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setStyle("-fx-fill: #6c757d;");
        
        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(350);
        
        // Name field
        nameField = createTextField("Full Name");
        nameField.setPromptText("Enter your full name");
        
        // Email field
        emailField = createTextField("Email Address");
        emailField.setPromptText("Enter your email");
        
        // Password field
        passwordField = createPasswordField("Password");
        passwordField.setPromptText("Create a strong password");
        
        // Password confirmation field
        confirmPasswordField = createPasswordField("Confirm Password");
        confirmPasswordField.setPromptText("Confirm your password");
        
        // Password strength indicator
        VBox passwordStrengthContainer = new VBox(5);
        passwordStrength = new ProgressBar(0);
        passwordStrength.setProgress(0);
        passwordStrength.setPrefWidth(350);
        passwordStrength.setStyle("-fx-accent: #dc3545;");
        
        passwordStrengthLabel = new Label("");
        passwordStrengthLabel.setFont(Font.font("Arial", 12));
        
        passwordStrengthContainer.getChildren().addAll(passwordStrength, passwordStrengthLabel);
        
        // Status label
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(350);
        
        // Buttons
        primaryButton = new Button("Sign Up");
        primaryButton.setPrefSize(350, 45);
        primaryButton.setStyle(createPrimaryButtonStyle());
        primaryButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        secondaryLink = new Hyperlink("Already have an account? Sign In");
        secondaryLink.setFont(Font.font("Arial", 12));
        
        // Add components to form
        formContainer.getChildren().addAll(
            nameField, emailField, passwordField, confirmPasswordField,
            passwordStrengthContainer, statusLabel, primaryButton, secondaryLink
        );
        
        // Add all to main container
        mainContainer.getChildren().addAll(title, subtitle, formContainer);
        
        // Setup event handlers
        setupSignupEventHandlers();
        
        // Setup real-time validation
        setupRealTimeValidation();
        
        // Show scene
        Scene scene = new Scene(mainContainer, 450, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/auth.css").toExternalForm());
        
        primaryStage.setTitle("Blogging App - Sign Up");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void showLoginForm() {
        isSignupMode = false;
        
        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #f8f9fa;");
        
        // Title
        Text title = new Text("Welcome Back");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-fill: #2c3e50;");
        
        // Subtitle
        Text subtitle = new Text("Sign in to continue to your account");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setStyle("-fx-fill: #6c757d;");
        
        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(350);
        
        // Email field
        emailField = createTextField("Email Address");
        emailField.setPromptText("Enter your email");
        
        // Password field
        passwordField = createPasswordField("Password");
        passwordField.setPromptText("Enter your password");
        
        // Status label
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(350);
        
        // Buttons
        primaryButton = new Button("Sign In");
        primaryButton.setPrefSize(350, 45);
        primaryButton.setStyle(createPrimaryButtonStyle());
        primaryButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        
        secondaryLink = new Hyperlink("Don't have an account? Sign Up");
        secondaryLink.setFont(Font.font("Arial", 12));
        
        Hyperlink forgotPasswordLink = new Hyperlink("Forgot Password?");
        forgotPasswordLink.setFont(Font.font("Arial", 12));
        forgotPasswordLink.setOnAction(e -> handleForgotPassword());
        
        buttonContainer.getChildren().addAll(secondaryLink, forgotPasswordLink);
        
        // Add components to form
        formContainer.getChildren().addAll(emailField, passwordField, statusLabel, primaryButton, buttonContainer);
        
        // Add all to main container
        mainContainer.getChildren().addAll(title, subtitle, formContainer);
        
        // Setup event handlers
        setupLoginEventHandlers();
        
        // Setup real-time validation
        setupRealTimeValidation();
        
        // Show scene
        Scene scene = new Scene(mainContainer, 450, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/auth.css").toExternalForm());
        
        primaryStage.setTitle("Blogging App - Sign In");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void showDashboard(UserEntity user) {
        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #f8f9fa;");
        
        // Welcome message
        Text welcomeText = new Text("Welcome to Blogging App!");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeText.setStyle("-fx-fill: #2c3e50;");
        
        Text userInfo = new Text("Logged in as: " + user.getUserName() + " (" + user.getEmail() + ")");
        userInfo.setFont(Font.font("Arial", 14));
        userInfo.setStyle("-fx-fill: #6c757d;");
        
        // User details
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(20);
        detailsGrid.setVgap(10);
        detailsGrid.setAlignment(Pos.CENTER);
        
        detailsGrid.add(new Label("User ID:"), 0, 0);
        detailsGrid.add(new Label(String.valueOf(user.getUserId())), 1, 0);
        
        detailsGrid.add(new Label("Name:"), 0, 1);
        detailsGrid.add(new Label(user.getUserName()), 1, 1);
        
        detailsGrid.add(new Label("Email:"), 0, 2);
        detailsGrid.add(new Label(user.getEmail()), 1, 2);
        
        detailsGrid.add(new Label("Role:"), 0, 3);
        detailsGrid.add(new Label(user.getRole()), 1, 3);
        
        detailsGrid.add(new Label("Member Since:"), 0, 4);
        detailsGrid.add(new Label(user.getCreatedAt().toLocalDate().toString()), 1, 4);
        
        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefSize(200, 40);
        logoutButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
        logoutButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        logoutButton.setOnAction(e -> handleLogout());
        
        // Add all to main container
        mainContainer.getChildren().addAll(welcomeText, userInfo, detailsGrid, logoutButton);
        
        // Show scene
        Scene scene = new Scene(mainContainer, 500, 400);
        primaryStage.setTitle("Blogging App - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    // ==================== EVENT HANDLERS ====================
    
    private void setupSignupEventHandlers() {
        primaryButton.setOnAction(e -> handleSignup());
        secondaryLink.setOnAction(e -> showLoginForm());
        
        // Password strength listener
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordStrength(newVal);
        });
    }
    
    private void setupLoginEventHandlers() {
        primaryButton.setOnAction(e -> handleLogin());
        secondaryLink.setOnAction(e -> showSignupForm());
    }
    
    private void setupRealTimeValidation() {
        // Email validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                if (isValidEmail(newVal.trim())) {
                    emailField.setStyle("-fx-border-color: #28a745;");
                } else {
                    emailField.setStyle("-fx-border-color: #dc3545;");
                }
            } else {
                emailField.setStyle("");
            }
        });
        
        // Password confirmation validation (signup only)
        if (isSignupMode && confirmPasswordField != null) {
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
    }
    
    private void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Disable button during processing
        primaryButton.setDisable(true);
        primaryButton.setText("Creating Account...");
        
        try {
            AuthenticationService.AuthResult result = authService.signup(name, email, password, confirmPassword);
            
            if (result.isSuccess()) {
                showSuccess("🎉 Account Created!", 
                    "Welcome to Blogging App, " + result.getUser().getUserName() + "!\n\nYou are now logged in.");
                showDashboard(result.getUser());
            } else {
                showError("Signup Failed", result.getMessage());
            }
            
        } catch (ValidationException e) {
            showError("Validation Error", e.getMessage());
            highlightErrorField(e.getErrorCode());
            
        } catch (AuthenticationException e) {
            showError("Authentication Error", e.getMessage());
            highlightErrorField(e.getErrorCode());
            
        } catch (DatabaseException e) {
            showError("Database Error", "Unable to create account. Please try again later.");
            
        } catch (Exception e) {
            showError("System Error", "An unexpected error occurred. Please try again.");
            
        } finally {
            primaryButton.setDisable(false);
            primaryButton.setText("Sign Up");
        }
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Disable button during processing
        primaryButton.setDisable(true);
        primaryButton.setText("Signing In...");
        
        try {
            AuthenticationService.AuthResult result = authService.login(email, password);
            
            if (result.isSuccess()) {
                showSuccess("✅ Login Successful!", "Welcome back, " + result.getUser().getUserName() + "!");
                showDashboard(result.getUser());
            } else {
                showError("Login Failed", result.getMessage());
            }
            
        } catch (ValidationException e) {
            showError("Validation Error", e.getMessage());
            highlightErrorField(e.getErrorCode());
            
        } catch (AuthenticationException e) {
            showError("Authentication Error", e.getMessage());
            highlightErrorField(e.getErrorCode());
            
        } catch (DatabaseException e) {
            showError("Database Error", "Login failed due to database issues. Please try again later.");
            
        } catch (Exception e) {
            showError("System Error", "An unexpected error occurred. Please try again.");
            
        } finally {
            primaryButton.setDisable(false);
            primaryButton.setText("Sign In");
        }
    }
    
    private void handleForgotPassword() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            showError("Email Required", "Please enter your email address first.");
            return;
        }
        
        if (!isValidEmail(email)) {
            showError("Invalid Email", "Please enter a valid email address.");
            return;
        }
        
        try {
            String resetMessage = authService.requestPasswordReset(email);
            showInfo("Password Reset", resetMessage);
            
        } catch (AuthenticationException | DatabaseException e) {
            showError("Reset Failed", "Failed to process password reset. Please try again.");
        }
    }
    
    private void handleLogout() {
        authService.logout();
        showSignupForm();
    }
    
    // ==================== UTILITY METHODS ====================
    
    private TextField createTextField(String label) {
        TextField field = new TextField();
        field.setPrefSize(350, 40);
        field.setStyle("-fx-border-radius: 5; -fx-border-color: #ced4da; -fx-padding: 10;");
        field.setFont(Font.font("Arial", 14));
        return field;
    }
    
    private PasswordField createPasswordField(String label) {
        PasswordField field = new PasswordField();
        field.setPrefSize(350, 40);
        field.setStyle("-fx-border-radius: 5; -fx-border-color: #ced4da; -fx-padding: 10;");
        field.setFont(Font.font("Arial", 14));
        return field;
    }
    
    private String createPrimaryButtonStyle() {
        return "-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;";
    }
    
    private void updatePasswordStrength(String password) {
        double strength = calculatePasswordStrength(password);
        passwordStrength.setProgress(strength);
        
        if (strength < 0.3) {
            passwordStrength.setStyle("-fx-accent: #dc3545;");
            passwordStrengthLabel.setText("Weak");
            passwordStrengthLabel.setStyle("-fx-text-fill: #dc3545;");
        } else if (strength < 0.7) {
            passwordStrength.setStyle("-fx-accent: #ffc107;");
            passwordStrengthLabel.setText("Medium");
            passwordStrengthLabel.setStyle("-fx-text-fill: #ffc107;");
        } else {
            passwordStrength.setStyle("-fx-accent: #28a745;");
            passwordStrengthLabel.setText("Strong");
            passwordStrengthLabel.setStyle("-fx-text-fill: #28a745;");
        }
        
        if (password.isEmpty()) {
            passwordStrengthLabel.setText("");
            passwordStrength.setProgress(0);
        }
    }
    
    private double calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) return 0.0;
        
        double strength = 0.0;
        
        if (password.length() >= 8) strength += 0.3;
        if (password.length() >= 12) strength += 0.2;
        if (password.matches(".*[a-z].*")) strength += 0.2;
        if (password.matches(".*[A-Z].*")) strength += 0.2;
        if (password.matches(".*[0-9].*")) strength += 0.1;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) strength += 0.1;
        
        return Math.min(strength, 1.0);
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    private void highlightErrorField(String errorCode) {
        // Reset all field styles
        if (nameField != null) nameField.setStyle("");
        emailField.setStyle("");
        passwordField.setStyle("");
        if (confirmPasswordField != null) confirmPasswordField.setStyle("");
        
        // Highlight specific field based on error
        switch (errorCode) {
            case "NAME_REQUIRED":
            case "INVALID_NAME":
                if (nameField != null) {
                    nameField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                    nameField.requestFocus();
                }
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
                if (confirmPasswordField != null) {
                    confirmPasswordField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                    confirmPasswordField.requestFocus();
                }
                break;
            case "EMAIL_EXISTS":
                emailField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                emailField.requestFocus();
                break;
            case "USER_NOT_FOUND":
                emailField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                emailField.requestFocus();
                break;
            case "INVALID_PASSWORD":
                passwordField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
                passwordField.requestFocus();
                break;
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ==================== MAIN METHOD ====================
    
    public static void main(String[] args) {
        launch(args);
    }
}
