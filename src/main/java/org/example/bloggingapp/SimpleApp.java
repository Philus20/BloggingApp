package org.example.bloggingapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleApp extends Application {
    @Override
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.getChildren().add(new Label("📊 Blogging App - Simple Test"));
        root.getChildren().add(new Label("Dashboard loading test..."));
        
        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Blogging App - Test");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
}
