package org.example.bloggingapp.Demo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Quick Database Schema Test
 */
public class QuickSchemaTest {
    
    public static void main(String[] args) {
        try {
            // Connect to database
            String url = "jdbc:postgresql://localhost:5432/blogging_db";
            String userName = "postgres";
            String password = "postgres";
            
            Connection connection = DriverManager.getConnection(url, userName, password);
            System.out.println("✅ Connected to database: " + connection.getMetaData().getURL());
            
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Get actual table and column info
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            System.out.println("\n=== ACTUAL DATABASE SCHEMA ===");
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("\n📋 Table: " + tableName);
                
                // Get columns for this table
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                
                System.out.println("   Columns:");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    System.out.println("     📄 " + columnName);
                }
                columns.close();
                
                // If it's the users table, show sample data
                if (tableName.equalsIgnoreCase("users")) {
                    System.out.println("\n   Sample data:");
                    try {
                        ResultSet sampleData = connection.createStatement().executeQuery("SELECT * FROM users LIMIT 1");
                        if (sampleData.next()) {
                            ResultSetMetaData rsmd = sampleData.getMetaData();
                            int columnCount = rsmd.getColumnCount();
                            
                            for (int i = 1; i <= columnCount; i++) {
                                String colName = rsmd.getColumnName(i);
                                Object value = sampleData.getObject(i);
                                System.out.println("     " + colName + ": " + (value != null ? value : "NULL"));
                            }
                        }
                        sampleData.close();
                    } catch (Exception e) {
                        System.err.println("   Error getting sample data: " + e.getMessage());
                    }
                }
            }
            
            tables.close();
            connection.close();
            
            System.out.println("\n=== EXPECTED VS ACTUAL ===");
            System.out.println("Expected columns: user_id, user_name, email, password, role, created_at");
            System.out.println("If you see different column names above, database needs to be recreated with correct schema");
            
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
