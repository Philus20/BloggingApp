package org.example.bloggingapp.Demo;

import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Database.factories.ServiceFactory;
import org.example.bloggingapp.Services.UserService;
import org.example.bloggingapp.Services.AuthenticationService;
import org.example.bloggingapp.Exceptions.DatabaseException;

import java.util.List;

/**
 * Utility to migrate existing plain text passwords to secure hashed passwords
 */
public class PasswordMigrationTool {
    
    public static void main(String[] args) {
        try {
            ServiceFactory serviceFactory = ServiceFactory.getInstance();
            UserService userService = serviceFactory.getUserService();
            AuthenticationService authService = new AuthenticationService(userService);
            
            System.out.println("=== PASSWORD MIGRATION TOOL ===\n");
            
            // Get all users
            List<UserEntity> users = userService.findAll();
            System.out.println("Found " + users.size() + " users to check\n");
            
            int migratedCount = 0;
            int alreadyHashedCount = 0;
            int errorCount = 0;
            
            for (UserEntity user : users) {
                System.out.println("Processing user: " + user.getUserName() + " (" + user.getEmail() + ")");
                
                String currentPassword = user.getPassword();
                boolean needsMigration = shouldMigratePassword(currentPassword);
                
                if (needsMigration) {
                    System.out.println("  ⚠️ Password appears to be plain text - migrating to secure hash");
                    
                    try {
                        // Hash the current plain text password
                        String hashedPassword = authService.hashPassword(currentPassword);
                        
                        // Update user with hashed password
                        user.setPassword(hashedPassword);
                        userService.update(user.getUserId(), user);
                        
                        System.out.println("  ✅ Successfully migrated password to hash");
                        migratedCount++;
                        
                    } catch (Exception e) {
                        System.err.println("  ❌ Failed to migrate password: " + e.getMessage());
                        errorCount++;
                    }
                } else {
                    System.out.println("  ✅ Password already appears to be hashed");
                    alreadyHashedCount++;
                }
                
                System.out.println();
            }
            
            // Summary
            System.out.println("=== MIGRATION SUMMARY ===");
            System.out.println("Total users processed: " + users.size());
            System.out.println("Passwords migrated: " + migratedCount);
            System.out.println("Already hashed: " + alreadyHashedCount);
            System.out.println("Errors: " + errorCount);
            
            if (migratedCount > 0) {
                System.out.println("\n✅ Migration completed successfully!");
                System.out.println("💡 All passwords are now securely hashed.");
                System.out.println("🔒 Users can now log in with their existing passwords.");
            } else {
                System.out.println("\n✅ All passwords were already securely hashed!");
            }
            
        } catch (DatabaseException e) {
            System.err.println("Database error during migration: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error during migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Determine if a password needs to be migrated to hash format
     */
    private static boolean shouldMigratePassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // If password is very short, likely plain text
        if (password.length() < 10) {
            return true;
        }
        
        // If password contains common plain text patterns
        if (password.matches(".*[a-zA-Z].*") && 
            password.length() < 20 && 
            !password.matches("^[A-Za-z0-9+/]+$")) {
            return true;
        }
        
        // If password doesn't look like Base64 encoded hash
        if (!password.matches("^[A-Za-z0-9+/]+$") || password.length() < 30) {
            return true;
        }
        
        return false;
    }
}
