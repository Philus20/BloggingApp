package org.example.bloggingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFeedTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainFeedTest.class.getResource("/org/example/bloggingapp/Screens/fxml/main_feed.fxml"));
            Parent root = fxmlLoader.load();
            
            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("📱 Blogging Platform - Main Feed");
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            System.err.println("❌ Error loading main feed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
