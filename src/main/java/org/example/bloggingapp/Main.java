package org.example.bloggingapp;

import org.example.bloggingapp.Database.DbInterfaces.IConnection;
import org.example.bloggingapp.Database.factories.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main {


    public static void main(String[] args) {

        System.out.println("Starting Demo JDBC Program...");
        String url = "jdbc:postgresql://localhost:5432/blogging_db";
        String username = "postgres";
        String password = "postgres";
        String query1 = "SELECT * FROM users";

        try (
                Connection con = DriverManager.getConnection(url, username, password);
                PreparedStatement pst = con.prepareStatement(query1);
                ResultSet rs = pst.executeQuery();
        ) {
            System.out.println("Connection established successfully.");
            int columnCount = rs.getMetaData().getColumnCount();

            while(rs.next()) {
                System.out.print(rs.getString("user_name") + "\t");
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("Hello World!");

        IConnection connection = new ConnectionFactory();
           Connection con= connection.createConnection();
           String query = "SELECT * FROM users";

        try (
                PreparedStatement pst = con.prepareStatement(query);
                ResultSet rs = pst.executeQuery();
        ) {
            System.out.println("Connection established successfully.");
            int columnCount = rs.getMetaData().getColumnCount();

            while(rs.next()) {
                System.out.print(rs.getString("user_name") + "\t");
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }






//        System.out.println("Created user with ID: " + user.getUserId());
    }
}
