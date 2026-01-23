package org.example.bloggingapp.Demo;

import org.example.bloggingapp.Database.factories.ConnectionFactory;
import org.example.bloggingapp.Database.factories.ServiceFactory;
import org.example.bloggingapp.Services.UserService;
import org.example.bloggingapp.Models.UserEntity;

import java.sql.Connection;
import java.util.List;

/**
 * Database Connection Diagnostic Tool
 */
public class DatabaseConnectionCheck {
    
    public static void main(String[] args) {
        System.out.println("=== DATABASE CONNECTION DIAGNOSTIC ===\n");
        
        // 1. Test basic connection
        System.out.println("1. Testing database connection...");
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ Database connection successful!");
                System.out.println("   Connection URL: " + connection.getMetaData().getURL());
                System.out.println("   Database name: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("   Database version: " + connection.getMetaData().getDatabaseProductVersion());
                connection.close();
            } else {
                System.out.println("❌ Database connection failed - connection is null or closed");
            }
        } catch (Exception e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n2. Testing UserService...");
        try {
            ServiceFactory serviceFactory = ServiceFactory.getInstance();
            UserService userService = serviceFactory.getUserService();
            List<UserEntity> users = userService.findAll();
            System.out.println("✅ UserService.findAll() successful!");
            System.out.println("   Found " + users.size() + " users in database");
            
            // Try to find a specific user
            try {
                UserEntity user = userService.findByEmail("alice@example.com");
                if (user != null) {
                    System.out.println("✅ User 'alice@example.com' found!");
                    System.out.println("   User ID: " + user.getUserId());
                    System.out.println("   User Name: " + user.getUserName());
                } else {
                    System.out.println("⚠️ User 'alice@example.com' not found in database");
                }
            } catch (Exception e) {
                System.err.println("❌ Error finding user 'alice@example.com': " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ UserService error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n3. Checking database name consistency...");
        System.out.println("   Expected: blogging_db");
        System.out.println("   ConnectionFactory URL: jdbc:postgresql://localhost:5432/blogging_db");
        System.out.println("   If you're seeing 'BloggingDb' in errors, the application needs to be recompiled!");
        
        System.out.println("\n=== DIAGNOSTIC COMPLETE ===");
        
        System.out.println("\n🔧 TROUBLESHOOTING STEPS:");
        System.out.println("1. Recompile the application (clean and build)");
        System.out.println("2. Restart the application server/IDE");
        System.out.println("3. Clear any cached compiled classes");
        System.out.println("4. Verify the database 'blogging_db' exists in PostgreSQL");
        System.out.println("5. Check PostgreSQL is running on localhost:5432");
    }
}
