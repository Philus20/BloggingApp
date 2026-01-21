package org.example.bloggingapp;

import org.example.bloggingapp.Database.DbInterfaces.IConnection;
import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.Repositories.UserRepository;
import org.example.bloggingapp.Database.Services.UserService;
import org.example.bloggingapp.Database.factories.ConnectionFactory;
import org.example.bloggingapp.Models.UserEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class Main {


    public static void main(String[] args) {
        System.out.println("Hello World!");

//        IConnection connection = new ConnectionFactory();
//           Connection con= connection.createConnection();
//           String query = "SELECT * FROM \"Post\"";
//
//        try (
//                PreparedStatement pst = con.prepareStatement(query);
//                ResultSet rs = pst.executeQuery();
//        ) {
//            System.out.println("Connection established successfully.");
//            int columnCount = rs.getMetaData().getColumnCount();
//
//            while(rs.next()) {
//                System.out.print(rs);
//                System.out.print(rs.getString("content") + "\t");
//                System.out.println();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }



        IService<UserEntity> userService = new UserService(new UserRepository());

//    public UserEntity( String userName, String email, String password, String role, LocalDateTime createdAt) {

        UserEntity user = new UserEntity("john_doe"," teo@gmail.com","password123","admin", LocalDateTime.now());
//userService.findAll();

        System.out.println("Created user with ID: " + user.getUserId());
    }
}
