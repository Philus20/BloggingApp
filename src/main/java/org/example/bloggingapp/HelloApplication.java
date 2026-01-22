package org.example.bloggingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Launch Dashboard as default route
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/bloggingapp/Screens/fxml/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("📊 Blogging App - Dashboard");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
}
