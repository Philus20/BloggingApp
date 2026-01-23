module org.example.bloggingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    opens org.example.bloggingapp to javafx.fxml;
    opens org.example.bloggingapp.Models to javafx.fxml;
    opens org.example.bloggingapp.controller to javafx.fxml;
    opens org.example.bloggingapp.Dashboard to javafx.fxml;
    opens org.example.bloggingapp.Services to javafx.fxml;
    opens org.example.bloggingapp.Database.Repositories to javafx.fxml;
    opens org.example.bloggingapp.Database.factories to javafx.fxml;
    opens org.example.bloggingapp.Cache to javafx.fxml;
    
    exports org.example.bloggingapp;
    exports org.example.bloggingapp.Models;
    exports org.example.bloggingapp.Dashboard;
    exports org.example.bloggingapp.Services;
    exports org.example.bloggingapp.Database.Repositories;
    exports org.example.bloggingapp.Database.factories;
    exports org.example.bloggingapp.Cache;
    exports org.example.bloggingapp.Database.DbInterfaces;
    opens org.example.bloggingapp.Database.DbInterfaces to javafx.fxml;
    exports org.example.bloggingapp.controller;
}