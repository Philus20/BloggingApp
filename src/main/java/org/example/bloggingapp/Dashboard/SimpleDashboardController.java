package org.example.bloggingapp.Dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.bloggingapp.Services.PostService;
import org.example.bloggingapp.Services.UserService;
import org.example.bloggingapp.Models.PostEntity;

import java.io.IOException;

/**
 * Simple Dashboard Controller for managing posts
 */
public class SimpleDashboardController {
    
    @FXML private TableView<PostEntity> postsTable;
    @FXML private TableColumn<PostEntity, Integer> postIdColumn;
    @FXML private TableColumn<PostEntity, String> titleColumn;
    @FXML private TableColumn<PostEntity, String> contentColumn;
    @FXML private TableColumn<PostEntity, Integer> userIdColumn;
    @FXML private TableColumn<PostEntity, String> createdAtColumn;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField userIdField;
    @FXML private Button createPostButton;
    @FXML private Button refreshPostsButton;
    
    // Navigation Buttons
    @FXML private Button mainFeedButton;
    @FXML private Button loginButton;
    @FXML private Button signupButton;
    
    private PostService postService;
    private UserService userService;
    private ObservableList<PostEntity> postsData;
    
    @FXML
    public void initialize() {
        System.out.println("🚀 Initializing SimpleDashboardController");
        
        try {
            // Initialize services
            this.postService = org.example.bloggingapp.Database.factories.ServiceFactory.getInstance().getPostService();
            this.userService = org.example.bloggingapp.Database.factories.ServiceFactory.getInstance().getUserService();
            
            // Initialize data
            postsData = FXCollections.observableArrayList();
            
            // Setup table columns
            setupTableColumns();
            
            // Load initial data
            loadPosts();
            
            System.out.println("✅ SimpleDashboardController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize SimpleDashboardController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupTableColumns() {
        postIdColumn.setCellValueFactory(new PropertyValueFactory<>("postId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
    }
    
    @FXML
    private void loadPosts() {
        try {
            postsData.clear();
            // Add sample data for now
            postsData.add(new PostEntity(1, "Sample Post 1", "This is a sample post", 
                java.time.LocalDateTime.now(), 1, "Published", 10, "Admin"));
            postsData.add(new PostEntity(2, "Sample Post 2", "Another sample post", 
                java.time.LocalDateTime.now().minusHours(1), 2, "Published", 5, "User"));
            
            postsTable.setItems(postsData);
            System.out.println("📋 Loaded " + postsData.size() + " posts");
        } catch (Exception e) {
            System.err.println("❌ Error loading posts: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRefreshPosts() {
        loadPosts();
    }
    
    @FXML
    private void handleCreatePost() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String userIdText = userIdField.getText().trim();
        
        if (title.isEmpty() || content.isEmpty() || userIdText.isEmpty()) {
            showAlert("Error", "Please fill all fields");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdText);
            PostEntity newPost = new PostEntity();
            newPost.setTitle(title);
            newPost.setContent(content);
            newPost.setUserId(userId);
            newPost.setCreatedAt(java.time.LocalDateTime.now());
            newPost.setStatus("Published");
            
            postsData.add(0, newPost);
            
            // Clear form
            titleField.clear();
            contentArea.clear();
            userIdField.clear();
            
            showAlert("Success", "Post created successfully!");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid User ID");
        } catch (Exception e) {
            showAlert("Error", "Failed to create post: " + e.getMessage());
        }
    }
    
    // ==================== NAVIGATION METHODS ====================
    
    @FXML
    private void goToMainFeed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/screens/MainFeed/MainFeed.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) mainFeedButton.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("📱 Blogging Platform - Main Feed");
            stage.setScene(scene);
            stage.show();
            
            System.out.println("📱 Navigated to Main Feed");
        } catch (IOException e) {
            System.err.println("❌ Failed to load Main Feed: " + e.getMessage());
            showAlert("Navigation Error", "Failed to load Main Feed. Please try again.");
        }
    }
    
    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/Screens/LoginResources/LoginPage.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Blogging App - Login");
            stage.setScene(scene);
            stage.show();
            
            System.out.println("🔐 Navigated to Login Page");
        } catch (IOException e) {
            System.err.println("❌ Failed to load Login: " + e.getMessage());
            showAlert("Navigation Error", "Failed to load Login page. Please try again.");
        }
    }
    
    @FXML
    private void goToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/Screens/Signup/Signup.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) signupButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 650);
            stage.setTitle("Blogging App - Sign Up");
            stage.setScene(scene);
            stage.show();
            
            System.out.println("📝 Navigated to Sign Up Page");
        } catch (IOException e) {
            System.err.println("❌ Failed to load Sign Up: " + e.getMessage());
            showAlert("Navigation Error", "Failed to load Sign Up page. Please try again.");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
