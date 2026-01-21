module org.example.bloggingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    opens org.example.bloggingapp to javafx.fxml;
    opens org.example.bloggingapp.Models to javafx.fxml;
    opens org.example.bloggingapp.controller to javafx.fxml;
    
    exports org.example.bloggingapp;
    exports org.example.bloggingapp.Models;
}