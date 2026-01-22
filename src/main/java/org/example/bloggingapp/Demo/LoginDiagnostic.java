package org.example.bloggingapp.Demo;

import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Database.factories.ServiceFactory;
import org.example.bloggingapp.Services.UserService;
import org.example.bloggingapp.Services.AuthenticationService;
import org.example.bloggingapp.Exceptions.DatabaseException;

import java.util.List;

/**
 * Diagnostic tool to troubleshoot login issues
 */
public class LoginDiagnostic {
    
    public static void main(String[] args) {
        try {
            // Initialize services
            ServiceFactory serviceFactory = ServiceFactory.getInstance();
            UserService userService = serviceFactory.getUserService();
            AuthenticationService authService = new AuthenticationService(userService);
            
            System.out.println("=== LOGIN DIAGNOSTIC TOOL ===\n");
            
            // 1. Check all users in database
            System.out.println("1. Checking all users in database:");
            List<UserEntity> allUsers = userService.findAll();
            System.out.println("Total users found: " + allUsers.size());
            
            for (UserEntity user : allUsers) {
                System.out.println("  - ID: " + user.getUserId());
                System.out.println("    Name: " + user.getUserName());
                System.out.println("    Email: " + user.getEmail());
                System.out.println("    Role: " + user.getRole());
                System.out.println("    Created: " + user.getCreatedAt());
                System.out.println("    Password length: " + user.getPassword().length());
                System.out.println("    Password starts with: " + user.getPassword().substring(0, Math.min(10, user.getPassword().length())) + "...");
                System.out.println("    Is hashed (contains Base64 chars): " + user.getPassword().matches(".*[A-Za-z0-9+/=].*"));
                System.out.println();
            }
            
            // 2. Test specific email lookup
            String testEmail = "teo@gmail.com";
            System.out.println("2. Testing email lookup for: " + testEmail);
            UserEntity foundUser = userService.findByEmail(testEmail);
            
            if (foundUser != null) {
                System.out.println("  ✅ User found:");
                System.out.println("    ID: " + foundUser.getUserId());
                System.out.println("    Name: " + foundUser.getUserName());
                System.out.println("    Email: " + foundUser.getEmail());
                System.out.println("    Password: " + foundUser.getPassword());
                
                // 3. Test password hashing
                System.out.println("\n3. Testing password hashing:");
                String testPassword = "password123"; // Common test password
                String hashedPassword = authService.hashPassword(testPassword);
                System.out.println("  Test password: " + testPassword);
                System.out.println("  Hashed password: " + hashedPassword);
                System.out.println("  Stored password: " + foundUser.getPassword());
                System.out.println("  Passwords match: " + hashedPassword.equals(foundUser.getPassword()));
                
                // 4. Test different common passwords
                System.out.println("\n4. Testing common passwords:");
                String[] commonPasswords = {"password", "123456", "admin", "teo123", "Password123!"};
                
                for (String pwd : commonPasswords) {
                    String hash = authService.hashPassword(pwd);
                    boolean matches = hash.equals(foundUser.getPassword());
                    System.out.println("  '" + pwd + "' -> " + (matches ? "✅ MATCH" : "❌ No match"));
                }
                
                // 5. Test if stored password is plain text
                System.out.println("\n5. Testing if stored password is plain text:");
                boolean isPlainText = testPassword.equals(foundUser.getPassword());
                System.out.println("  Plain text match: " + isPlainText);
                
                if (isPlainText) {
                    System.out.println("  ⚠️ WARNING: Password is stored in plain text!");
                    System.out.println("  💡 Recommendation: Update user passwords to use secure hashing");
                }
                
            } else {
                System.out.println("  ❌ User not found with email: " + testEmail);
            }
            
            // 6. Test authentication with different scenarios
            System.out.println("\n6. Testing authentication scenarios:");
            if (foundUser != null) {
                // Test with plain text password (if stored as plain text)
                if (foundUser.getPassword().equals("password123")) {
                    System.out.println("  Testing with plain text 'password123':");
                    try {
                        var result = authService.login(testEmail, "password123");
                        System.out.println("    ✅ Login successful: " + result.getMessage());
                    } catch (Exception e) {
                        System.out.println("    ❌ Login failed: " + e.getMessage());
                    }
                }
                
                // Test with hashed password
                System.out.println("  Testing with hashed password equivalent:");
                try {
                    var result = authService.login(testEmail, "Password123!"); // Try strong password
                    System.out.println("    ✅ Login successful: " + result.getMessage());
                } catch (Exception e) {
                    System.out.println("    ❌ Login failed: " + e.getMessage());
                }
            }
            
            System.out.println("\n=== DIAGNOSTIC COMPLETE ===");
            
        } catch (DatabaseException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
