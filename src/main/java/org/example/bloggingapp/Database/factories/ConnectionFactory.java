package org.example.bloggingapp.Database.factories;

import org.example.bloggingapp.Database.DbInterfaces.IConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory implements IConnection {


    public Connection createConnection() {
        try {
            String userName = "postgres";
            String password = "postgres";
            String url = "jdbc:postgresql://localhost:5432/BloggingDb";
            Connection connection =
                    DriverManager.getConnection(url, userName, password);

            System.out.println("Connected to the database successfully.");
            return connection;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database connection", e);
        }
    }
}
