package org.example.bloggingapp.Demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple Database Schema Check - Direct SQL approach
 */
public class SimpleSchemaCheck {
    
    public static void main(String[] args) {
        System.out.println("=== SIMPLE DATABASE SCHEMA CHECK ===\n");
        
        try {
            // Connect to database
            String url = "jdbc:postgresql://localhost:5432/blogging_db";
            String userName = "postgres";
            String password = "postgres";
            
            Connection connection = DriverManager.getConnection(url, userName, password);
            System.out.println("✅ Connected to database: " + connection.getMetaData().getURL());
            
            // Check users table structure with direct SQL
            System.out.println("\n1. CHECKING 'users' TABLE STRUCTURE:");
            try {
                ResultSet structure = connection.createStatement().executeQuery(
                    "SELECT column_name, data_type FROM information_schema.columns " +
                    "WHERE table_name = 'users' ORDER BY ordinal_position"
                );
                
                System.out.println("   Columns found:");
                while (structure.next()) {
                    String columnName = structure.getString("column_name");
                    String dataType = structure.getString("data_type");
                    System.out.println("   📄 " + columnName + " (" + dataType + ")");
                }
                structure.close();
                
            } catch (Exception e) {
                System.err.println("❌ Error checking table structure: " + e.getMessage());
            }
            
            // Try to query users table directly
            System.out.println("\n2. TESTING DIRECT QUERY:");
            try {
                ResultSet testQuery = connection.createStatement().executeQuery("SELECT * FROM users LIMIT 1");
                
                // Get column count and names
                int columnCount = testQuery.getMetaData().getColumnCount();
                System.out.println("   Columns returned by query: " + columnCount);
                
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = testQuery.getMetaData().getColumnName(i);
                    System.out.println("   📋 Column " + i + ": " + columnName);
                }
                
                // Try to access specific columns that our code expects
                try {
                    if (testQuery.next()) {
                        System.out.println("\n   Testing column access:");
                        System.out.println("   user_id: " + testQuery.getString("user_id"));
                        System.out.println("   userId: " + testQuery.getString("userId"));
                        System.out.println("   user_name: " + testQuery.getString("user_name"));
                        System.out.println("   userName: " + testQuery.getString("userName"));
                        System.out.println("   email: " + testQuery.getString("email"));
                        System.out.println("   role: " + testQuery.getString("role"));
                        System.out.println("   created_at: " + testQuery.getString("created_at"));
                        System.out.println("   createdAt: " + testQuery.getString("createdAt"));
                    }
                } catch (Exception e) {
                    System.err.println("   ❌ Error accessing columns: " + e.getMessage());
                }
                
                testQuery.close();
                
            } catch (Exception e) {
                System.err.println("❌ Error querying users table: " + e.getMessage());
            }
            
            connection.close();
            
            System.out.println("\n=== DIAGNOSTIC COMPLETE ===");
            
            System.out.println("\n🔧 SOLUTION:");
            System.out.println("1. If 'user_id' works but 'userId' doesn't, database has lowercase columns");
            System.out.println("2. If 'user_name' works but 'userName' doesn't, use lowercase in Java code");
            System.out.println("3. Update UserRepository to use actual column names from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
