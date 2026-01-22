package org.example.bloggingapp.Dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.bloggingapp.Cache.CacheManager;

import java.io.IOException;
import java.util.Objects;

/**
 * Main Dashboard Application for managing posts, viewing performance metrics, and performing optimizations
 */
public class DashboardApp extends Application {
    
    private DashboardController controller;
    
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize cache manager
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.start(1); // Cleanup every minute
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(DashboardApp.class.getResource("/org/example/bloggingapp/Screens/fxml/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        
        // Get controller reference
        controller = fxmlLoader.getController();
        
        // Configure stage
        stage.setTitle("Blogging App Dashboard - Cache Management & Performance");
        stage.setScene(scene);
        
        // Set application icon (if available)
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        // Set close handler
        stage.setOnCloseRequest(event -> {
            if (controller != null) {
                controller.shutdown();
            }
            cacheManager.stop();
        });
        
        stage.show();
    }
    
    @Override
    public void stop() {
        if (controller != null) {
            controller.shutdown();
        }
        CacheManager.getInstance().stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
