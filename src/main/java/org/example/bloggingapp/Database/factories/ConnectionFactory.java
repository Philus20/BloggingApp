package org.example.bloggingapp.Database.factories;

import org.example.bloggingapp.Database.DbInterfaces.IConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory implements IConnection {


    public Connection createConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/blogging_db";
            String username = "postgres";
            String password = "postgres";
            Connection connection =
                    DriverManager.getConnection(url, username, password);

            System.out.println("Connected to the database successfully.");
            return connection;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database connection", e);
        }
    }
}
