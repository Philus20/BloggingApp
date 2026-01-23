//package org.example.bloggingapp.Demo;
//
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
///**
// * Database Schema Diagnostic Tool - Checks actual database structure
// */
//public class DatabaseSchemaCheck {
//
//    public static void main(String[] args) {
//        System.out.println("=== DATABASE SCHEMA DIAGNOSTIC ===\n");
//
//        try {
//            // Connect to database
//            String url = "jdbc:postgresql://localhost:5432/blogging_db";
//            String userName = "postgres";
//            String password = "postgres";
//
//            Connection connection = DriverManager.getConnection(url, userName, password);
//            System.out.println("✅ Connected to database: " + connection.getMetaData().getURL());
//
//            DatabaseMetaData metaData = connection.getMetaData();
//
//            // Check tables
//            System.out.println("\n1. TABLES IN DATABASE:");
//            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"}, null);
//            while (tables.next()) {
//                String tableName = tables.getString("TABLE_NAME");
//                System.out.println("   📋 " + tableName);
//
//                // Check columns for each table
//                if (tableName.equalsIgnoreCase("users")) {
//                    System.out.println("\n2. COLUMNS IN 'users' TABLE:");
//                    ResultSet columns = metaData.getColumns(null, null, tableName, "%", null);
//                    while (columns.next()) {
//                        String columnName = columns.getString("COLUMN_NAME");
//                        String columnType = columns.getString("TYPE_NAME");
//                        System.out.println("   📄 " + columnName + " (" + columnType + ")");
//                    }
//                    columns.close();
//                }
//            }
//            tables.close();
//
//            // Check if users table has data
//            System.out.println("\n3. SAMPLE DATA FROM 'users' TABLE:");
//            try {
//                ResultSet sampleData = connection.createStatement().executeQuery("SELECT * FROM users LIMIT 3");
//                int columnCount = sampleData.getMetaData().getColumnCount();
//
//                // Print column headers
//                for (int i = 1; i <= columnCount; i++) {
//                    System.out.print(String.format("%-15s", sampleData.getMetaData().getColumnName(i)));
//                }
//                System.out.println();
//                System.out.println("-".repeat(80));
//
//                // Print sample data
//                while (sampleData.next()) {
//                    for (int i = 1; i <= columnCount; i++) {
//                        String value = sampleData.getString(i);
//                        System.out.print(String.format("%-15s", value != null ? value : "NULL"));
//                    }
//                    System.out.println();
//                }
//                sampleData.close();
//            } catch (Exception e) {
//                System.err.println("❌ Error querying users table: " + e.getMessage());
//            }
//
//            connection.close();
//
//            System.out.println("\n=== DIAGNOSTIC COMPLETE ===");
//
//            System.out.println("\n🔧 POSSIBLE ISSUES:");
//            System.out.println("1. Column names in database don't match Java code expectations");
//            System.out.println("2. Database was created with different naming convention");
//            System.out.println("3. Need to update database schema or Java mappings");
//
//        } catch (SQLException e) {
//            System.err.println("❌ Database error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
