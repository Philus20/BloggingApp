package org.example.bloggingapp.Services;

import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Database.Utils.RegexPatterns;
import org.example.bloggingapp.Exceptions.AuthenticationException;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.ValidationException;

import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * Professional authentication service with secure password handling
 * Provides signup, login, and session management functionality
 */
public class AuthenticationService {
    
    private final UserService userService;
    
    // Session management
    private static UserEntity currentUser;
    private static String sessionToken;
    private static LocalDateTime sessionExpiry;
    
    // Password hashing constants
    private static final String SALT = "BloggingApp_Salt_2024_Secure!";
    private static final int SESSION_DURATION_HOURS = 24;
    
    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Professional signup with comprehensive validation and secure password handling
     */
    public AuthResult signup(String name, String email, String password, String confirmPassword) 
            throws AuthenticationException, ValidationException, DatabaseException {
        
        // Input validation
        validateSignupInput(name, email, password, confirmPassword);
        
        try {
            // Check if user already exists
            UserEntity existingUser = userService.findByEmail(email);
            if (existingUser != null) {
                throw new AuthenticationException("EMAIL_EXISTS", "An account with this email already exists");
            }
            
            // Create new user with secure password
            UserEntity newUser = new UserEntity();
            newUser.setUserName(name.trim());
            newUser.setEmail(email.toLowerCase().trim());
            newUser.setPassword(hashPassword(password)); // Secure password hashing
            newUser.setRole("USER");
            newUser.setCreatedAt(LocalDateTime.now());
            
            // Save user to database
            UserEntity createdUser = userService.create(newUser);
            
            // Auto-login after successful signup
            login(createdUser.getEmail(), password);
            
            return new AuthResult(true, "Account created successfully", createdUser, sessionToken);
            
        } catch (ValidationException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("SIGNUP_ERROR", "Failed to create user account", e);
        }
    }
    
    /**
     * Professional login with secure authentication
     */
    public AuthResult login(String email, String password) 
            throws AuthenticationException, ValidationException, DatabaseException {
        
        // Input validation
        validateLoginInput(email, password);
        
        try {
            // Find user by email
            UserEntity user = userService.findByEmail(email.toLowerCase().trim());
            System.out.println("Found user: " + (user != null ? user.getUserName() + " (ID: " + user.getUserId() + ")" : "null"));
            
            if (user == null) {
                throw new AuthenticationException("USER_NOT_FOUND", "No account found with this email");
            }
            
            System.out.println("Attempting password verification for user: " + user.getUserName());
            System.out.println("Stored password hash: " + user.getPassword().substring(0, Math.min(10, user.getPassword().length())) + "...");
            
            // Verify password
            boolean passwordMatch = verifyPassword(password, user.getPassword());
            System.out.println("Password match: " + passwordMatch);
            
            if (!passwordMatch) {
                throw new AuthenticationException("INVALID_PASSWORD", "Incorrect password");
            }
            
            // Create session
            createSession(user);
            
            System.out.println("✅ Login successful for: " + user.getUserName() + " (ID: " + user.getUserId() + ")");
            
            return new AuthResult(true, "Login successful", user, sessionToken);
            
        } catch (ValidationException | DatabaseException e) {
            System.err.println("Validation/Database error during login: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (AuthenticationException e) {
            System.err.println("Authentication error during login: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            throw new AuthenticationException("DATABASE_ERROR", "Database error during login", e);
        }
    }
    
    /**
     * Logout and invalidate session
     */
    public void logout() {
        if (isLoggedIn()) {
            System.out.println("👋 Logging out user: " + currentUser.getUserName());
            currentUser = null;
            sessionToken = null;
            sessionExpiry = null;
        }
    }
    
    /**
     * Get current authenticated user
     */
    public UserEntity getCurrentUser() {
        // Check if session is still valid
        if (isSessionExpired()) {
            logout();
            return null;
        }
        return currentUser;
    }
    
    /**
     * Check if user is logged in with valid session
     */
    public boolean isLoggedIn() {
        return currentUser != null && !isSessionExpired();
    }
    
    /**
     * Validate session token
     */
    public boolean validateSession(String token) {
        return isLoggedIn() && sessionToken.equals(token);
    }
    
    /**
     * Change password with current password verification
     */
    public boolean changePassword(String currentPassword, String newPassword, String confirmPassword) 
            throws AuthenticationException, ValidationException, DatabaseException {
        
        if (!isLoggedIn()) {
            throw new AuthenticationException("NOT_LOGGED_IN", "You must be logged in to change password");
        }
        
        // Validate new password
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("PASSWORD_MISMATCH", "New passwords do not match");
        }
        
        if (!RegexPatterns.matches(newPassword, RegexPatterns.STRONG_PASSWORD)) {
            throw new ValidationException("WEAK_PASSWORD", 
                "Password must be at least 8 characters with uppercase, lowercase, numbers, and special characters");
        }
        
        // Verify current password
        if (!verifyPassword(currentPassword, currentUser.getPassword())) {
            throw new AuthenticationException("INVALID_CURRENT_PASSWORD", "Current password is incorrect");
        }
        
        try {
            // Update password
            currentUser.setPassword(hashPassword(newPassword));
            userService.update(currentUser.getUserId(), currentUser);
            
            System.out.println("✅ Password changed successfully for user: " + currentUser.getUserName());
            return true;
            
        } catch (Exception e) {
            throw new AuthenticationException("PASSWORD_CHANGE_ERROR", "Failed to change password", e);
        }
    }
    
    /**
     * Request password reset (simplified version)
     */
    public String requestPasswordReset(String email) throws AuthenticationException, DatabaseException {
        try {
            UserEntity user = userService.findByEmail(email.toLowerCase().trim());
            if (user == null) {
                // Don't reveal if email exists for security
                return "If an account with this email exists, a reset link has been sent.";
            }
            
            // Generate reset token (simplified - in real app, email would be sent)
            String resetToken = UUID.randomUUID().toString();
            
            System.out.println("🔑 Password reset requested for: " + email);
            System.out.println("📧 Reset token (demo): " + resetToken);
            
            return "If an account with this email exists, a reset link has been sent.";
            
        } catch (Exception e) {
            throw new AuthenticationException("RESET_REQUEST_ERROR", "Failed to process password reset", e);
        }
    }
    
    // ==================== PRIVATE METHODS ====================
    
    private void validateSignupInput(String name, String email, String password, String confirmPassword) 
            throws ValidationException {
        
        if (RegexPatterns.isNullOrEmpty(name)) {
            throw new ValidationException("NAME_REQUIRED", "Name is required");
        }
        
        if (RegexPatterns.isNullOrEmpty(email)) {
            throw new ValidationException("EMAIL_REQUIRED", "Email is required");
        }
        
        if (RegexPatterns.isNullOrEmpty(password)) {
            throw new ValidationException("PASSWORD_REQUIRED", "Password is required");
        }
        
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("PASSWORD_MISMATCH", "Passwords do not match");
        }
        
        if (!RegexPatterns.isLengthValid(name, 2, 50)) {
            throw new ValidationException("INVALID_NAME", "Name must be between 2 and 50 characters");
        }
        
        if (!RegexPatterns.matches(email, RegexPatterns.EMAIL)) {
            throw new ValidationException("INVALID_EMAIL", "Please enter a valid email address");
        }
        
        if (!RegexPatterns.matches(password, RegexPatterns.STRONG_PASSWORD)) {
            throw new ValidationException("WEAK_PASSWORD", 
                "Password must be at least 8 characters with uppercase, lowercase, numbers, and special characters");
        }
    }
    
    private void validateLoginInput(String email, String password) throws ValidationException {
        if (RegexPatterns.isNullOrEmpty(email)) {
            throw new ValidationException("EMAIL_REQUIRED", "Email is required");
        }
        
        if (RegexPatterns.isNullOrEmpty(password)) {
            throw new ValidationException("PASSWORD_REQUIRED", "Password is required");
        }
        
        if (!RegexPatterns.matches(email, RegexPatterns.EMAIL)) {
            throw new ValidationException("INVALID_EMAIL", "Please enter a valid email address");
        }
    }
    
    private void createSession(UserEntity user) {
        currentUser = user;
        sessionToken = generateSessionToken();
        sessionExpiry = LocalDateTime.now().plusHours(SESSION_DURATION_HOURS);
    }
    
    private boolean isSessionExpired() {
        return sessionExpiry == null || LocalDateTime.now().isAfter(sessionExpiry);
    }
    
    private String generateSessionToken() {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    }
    
    /**
     * Secure password hashing using SHA-256 with salt
     */
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + SALT;
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Verify password against stored hash (supports both plain text and hashed passwords)
     */
    private boolean verifyPassword(String inputPassword, String storedPassword) {
        try {
            // First try to verify as hashed password
            String inputHash = hashPassword(inputPassword);
            boolean hashMatches = inputHash.equals(storedPassword);
            
            if (hashMatches) {
                System.out.println("✅ Password verified using hash comparison");
                return true;
            }
            
            // If hash doesn't match, try plain text comparison (for backward compatibility)
            boolean plainTextMatches = inputPassword.equals(storedPassword);
            
            if (plainTextMatches) {
                System.out.println("⚠️ Password verified using plain text comparison (consider updating to hashed passwords)");
                return true;
            }
            
            System.out.println("❌ Password verification failed - neither hash nor plain text matched");
            return false;
            
        } catch (Exception e) {
            System.err.println("Error during password verification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== STATIC METHODS ====================
    
    /**
     * Get current user (static method for convenience)
     */
    public static UserEntity getCurrentAuthenticatedUser() {
        return currentUser;
    }
    
    /**
     * Check if any user is logged in (static method for convenience)
     */
    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Force logout (static method for convenience)
     */
    public static void forceLogout() {
        currentUser = null;
        sessionToken = null;
        sessionExpiry = null;
    }
    
    /**
     * Authentication result container
     */
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final UserEntity user;
        private final String sessionToken;
        
        public AuthResult(boolean success, String message, UserEntity user, String sessionToken) {
            this.success = success;
            this.message = message;
            this.user = user;
            this.sessionToken = sessionToken;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public UserEntity getUser() { return user; }
        public String getSessionToken() { return sessionToken; }
    }
}
