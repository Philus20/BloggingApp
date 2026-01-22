package org.example.bloggingapp.Demo;

import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Database.factories.ServiceFactory;
import org.example.bloggingapp.Services.UserService;

import java.util.List;

/**
 * Quick diagnostic to check user password formats
 */
public class QuickPasswordCheck {
    
    public static void main(String[] args) {
        try {
            ServiceFactory serviceFactory = ServiceFactory.getInstance();
            UserService userService = serviceFactory.getUserService();
            
            System.out.println("=== PASSWORD FORMAT CHECK ===\n");
            
            // Get all users
            List<UserEntity> users = userService.findAll();
            System.out.println("Found " + users.size() + " users:\n");
            
            for (UserEntity user : users) {
                System.out.println("User: " + user.getUserName() + " (" + user.getEmail() + ")");
                System.out.println("Password: " + user.getPassword());
                System.out.println("Length: " + user.getPassword().length());
                
                // Check if password looks like Base64 (hashed)
                boolean looksLikeHash = user.getPassword().matches("^[A-Za-z0-9+/]+$") && 
                                     user.getPassword().length() > 20;
                System.out.println("Appears to be hashed: " + looksLikeHash);
                System.out.println("---");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
