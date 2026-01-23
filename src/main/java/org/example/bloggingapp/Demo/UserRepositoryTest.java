package org.example.bloggingapp.Demo;

import org.example.bloggingapp.Database.Repositories.UserRepository;
import org.example.bloggingapp.Models.UserEntity;

import java.util.List;

public class UserRepositoryTest {
    
    public static void main(String[] args) {
        System.out.println("Testing UserRepository after table name fix...");
        
        UserRepository userRepository = new UserRepository();
        
        try {
            // Test findAll()
            System.out.println("\n=== Testing findAll() ===");
            List<UserEntity> users = userRepository.findAll();
            System.out.println("Found " + users.size() + " users:");
            
            for (UserEntity user : users) {
                System.out.println("  ID: " + user.getUserId() + 
                                 ", Name: " + user.getUserName() + 
                                 ", Email: " + user.getEmail());
            }
            
            // Test findByString() with bob@example.com
            System.out.println("\n=== Testing findByString() with bob@example.com ===");
            UserEntity user = userRepository.findByString("bob@example.com");
            if (user != null) {
                System.out.println("✅ Found user: " + user.getUserName() + " (" + user.getEmail() + ")");
            } else {
                System.out.println("❌ User not found");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Test completed ===");
    }
}
