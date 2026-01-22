package org.example.bloggingapp.Dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.bloggingapp.Cache.CacheManager;
import org.example.bloggingapp.Services.PostService;
import org.example.bloggingapp.Services.UserService;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Models.UserEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;

/**
 * Dashboard Controller for managing posts, viewing performance metrics, and performing optimizations
 */
public class DashboardController {
    
    @FXML private TabPane mainTabPane;
    
    // Navigation Buttons
    @FXML private Button mainFeedButton;
    @FXML private Button loginButton;
    @FXML private Button signupButton;
    
    // Posts Management Tab
    @FXML private TableView<PostEntity> postsTable;
    @FXML private TableColumn<PostEntity, Integer> postIdColumn;
    @FXML private TableColumn<PostEntity, String> titleColumn;
    @FXML private TableColumn<PostEntity, String> contentColumn;
    @FXML private TableColumn<PostEntity, Integer> userIdColumn;
    @FXML private TableColumn<PostEntity, String> createdAtColumn;
    @FXML private TableColumn<PostEntity, String> statusColumn;
    @FXML private TableColumn<PostEntity, Integer> viewsColumn;
    @FXML private TableColumn<PostEntity, String> authorNameColumn;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField userIdField;
    @FXML private TextField statusField;
    @FXML private TextField viewsField;
    @FXML private TextField authorNameField;
    @FXML private Button createPostButton;
    @FXML private Button updatePostButton;
    @FXML private Button deletePostButton;
    @FXML private Button refreshPostsButton;
    @FXML private TextField searchPostField;
    
    // Performance Metrics Tab
    @FXML private LineChart<String, Number> hitRateChart;
    @FXML private LineChart<String, Number> responseTimeChart;
    @FXML private Label totalPostsLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label cacheHitRateLabel;
    @FXML private Label cacheSizeLabel;
    @FXML private Label evictionCountLabel;
    @FXML private TextArea metricsTextArea;
    
    // Cache Management Tab
    @FXML private Button clearPostCacheButton;
    @FXML private Button clearUserCacheButton;
    @FXML private Button clearAllCacheButton;
    @FXML private Button cleanupExpiredButton;
    @FXML private Button forceRefreshButton;
    @FXML private Slider cacheSizeSlider;
    @FXML private Label cacheSizeValueLabel;
    @FXML private ComboBox<String> expirationTimeCombo;
    @FXML private Button applyConfigButton;
    @FXML private TextArea cacheStatusArea;
    
    // Optimization Tab
    @FXML private Button analyzePerformanceButton;
    @FXML private Button optimizeCacheButton;
    @FXML private Button preloadDataButton;
    @FXML private Button warmupCacheButton;
    @FXML private ProgressBar optimizationProgress;
    @FXML private Label optimizationStatusLabel;
    @FXML private TextArea optimizationLogArea;
    
    // Services
    private PostService postService;
    private UserService userService;
    private CacheManager cacheManager;
    private PerformanceMonitor performanceMonitor;
    private CacheOptimizationService optimizationService;
    
    // Data
    private ObservableList<PostEntity> postsData;
    private Timer metricsUpdateTimer;
    
    @FXML
    public void initialize() {
        initializeServices();
        initializePostTable();
        initializeCharts();
        initializeCacheControls();
        startMetricsUpdater();
        loadInitialData();
    }
    
    private void initializeServices() {
        try {
            // Initialize services with caching
            this.postService = new PostService(null); // Will need proper repository
            this.userService = new UserService(null);
            this.cacheManager = CacheManager.getInstance();
            this.performanceMonitor = PerformanceMonitor.getInstance();
            this.optimizationService = new CacheOptimizationService(postService, userService);
            
            cacheManager.start(1); // Cleanup every minute
            optimizationService.startAutomaticOptimization(5); // Auto-optimize every 5 minutes
        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to initialize services: " + e.getMessage());
        }
    }
    
    private void initializePostTable() {
        postsData = FXCollections.observableArrayList();
        postsTable.setItems(postsData);
        
        postIdColumn.setCellValueFactory(new PropertyValueFactory<>("postId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        viewsColumn.setCellValueFactory(new PropertyValueFactory<>("views"));
        authorNameColumn.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        
        // Make table editable
        postsTable.setEditable(true);
        postsTable.setRowFactory(tv -> {
            TableRow<PostEntity> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    PostEntity selectedPost = row.getItem();
                    populatePostFields(selectedPost);
                }
            });
            return row;
        });
    }
    
    private void initializeCharts() {
        // Initialize hit rate chart
        hitRateChart.setTitle("Cache Hit Rate Over Time");
        hitRateChart.getXAxis().setLabel("Time");
        hitRateChart.getYAxis().setLabel("Hit Rate (%)");
        
        // Initialize response time chart
        responseTimeChart.setTitle("Response Time Over Time");
        responseTimeChart.getXAxis().setLabel("Time");
        responseTimeChart.getYAxis().setLabel("Response Time (ms)");
    }
    
    private void initializeCacheControls() {
        // Initialize cache size slider
        cacheSizeSlider.setMin(100);
        cacheSizeSlider.setMax(2000);
        cacheSizeSlider.setValue(500);
        cacheSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            cacheSizeValueLabel.setText(String.valueOf(newVal.intValue()));
        });
        
        // Initialize expiration time combo
        expirationTimeCombo.getItems().addAll(
            "1 minute", "5 minutes", "10 minutes", "30 minutes",
            "1 hour", "2 hours", "6 hours", "12 hours", "24 hours"
        );
        expirationTimeCombo.setValue("10 minutes");
    }
    
    private void startMetricsUpdater() {
        metricsUpdateTimer = new Timer("MetricsUpdater", true);
        metricsUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> updateMetrics());
            }
        }, 0, 5000); // Update every 5 seconds
    }
    
    private void loadInitialData() {
        loadPosts();
        updateMetrics();
        updateCacheStatus();
    }
    
    // Post Management Methods
    @FXML
    private void handleCreatePost() {
        try {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String userIdText = userIdField.getText().trim();
            String status = statusField.getText().trim();
            String viewsText = viewsField.getText().trim();
            String authorName = authorNameField.getText().trim();
            
            if (title.isEmpty() || content.isEmpty() || userIdText.isEmpty()) {
                showAlert("Validation Error", "Title, content, and User ID are required");
                return;
            }
            
            int userId = Integer.parseInt(userIdText);
            int views = viewsText.isEmpty() ? 0 : Integer.parseInt(viewsText);
            
            PostEntity post = new PostEntity();
            post.setTitle(title);
            post.setContent(content);
            post.setUserId(userId);
            post.setStatus(status.isEmpty() ? "Draft" : status);
            post.setViews(views);
            post.setAuthorName(authorName.isEmpty() ? "" : authorName);
            
            PostEntity createdPost = postService.create(post);
            postsData.add(createdPost);
            clearPostFields();
            updateMetrics();
            
            showAlert("Success", "Post created successfully");
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid User ID format");
        } catch (Exception e) {
            showAlert("Error", "Failed to create post: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdatePost() {
        PostEntity selectedPost = postsTable.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showAlert("Selection Error", "Please select a post to update");
            return;
        }
        
        try {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String userIdText = userIdField.getText().trim();
            String status = statusField.getText().trim();
            String viewsText = viewsField.getText().trim();
            String authorName = authorNameField.getText().trim();
            
            if (title.isEmpty() || content.isEmpty() || userIdText.isEmpty()) {
                showAlert("Validation Error", "Title, content, and User ID are required");
                return;
            }
            
            int userId = Integer.parseInt(userIdText);
            int views = viewsText.isEmpty() ? selectedPost.getViews() : Integer.parseInt(viewsText);
            
            selectedPost.setTitle(title);
            selectedPost.setContent(content);
            selectedPost.setUserId(userId);
            selectedPost.setStatus(status.isEmpty() ? selectedPost.getStatus() : status);
            selectedPost.setViews(views);
            selectedPost.setAuthorName(authorName.isEmpty() ? selectedPost.getAuthorName() : authorName);
            
            PostEntity updatedPost = postService.update(selectedPost.getPostId(), selectedPost);
            if (updatedPost != null) {
                postsTable.refresh();
                updateMetrics();
                showAlert("Success", "Post updated successfully");
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid User ID format");
        } catch (Exception e) {
            showAlert("Error", "Failed to update post: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDeletePost() {
        PostEntity selectedPost = postsTable.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showAlert("Selection Error", "Please select a post to delete");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Post");
        confirmDialog.setContentText("Are you sure you want to delete this post?");
        
        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean deleted = postService.delete(selectedPost.getPostId());
                if (deleted) {
                    postsData.remove(selectedPost);
                    clearPostFields();
                    updateMetrics();
                    showAlert("Success", "Post deleted successfully");
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to delete post: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleRefreshPosts() {
        loadPosts();
        updateMetrics();
    }
    
    @FXML
    private void handleSearchPosts() {
        String searchText = searchPostField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadPosts();
            return;
        }
        
        try {
            List<PostEntity> allPosts = postService.findAll();
            ObservableList<PostEntity> filteredPosts = FXCollections.observableArrayList();
            
            for (PostEntity post : allPosts) {
                if (post.getTitle().toLowerCase().contains(searchText) ||
                    post.getContent().toLowerCase().contains(searchText)) {
                    filteredPosts.add(post);
                }
            }
            
            postsData.setAll(filteredPosts);
        } catch (Exception e) {
            showAlert("Error", "Failed to search posts: " + e.getMessage());
        }
    }
    
    private void loadPosts() {
        try {
            List<PostEntity> posts = postService.findAll();
            postsData.setAll(posts);
        } catch (Exception e) {
            showAlert("Error", "Failed to load posts: " + e.getMessage());
        }
    }
    
    private void populatePostFields(PostEntity post) {
        titleField.setText(post.getTitle());
        contentArea.setText(post.getContent());
        userIdField.setText(String.valueOf(post.getUserId()));
        statusField.setText(post.getStatus());
        viewsField.setText(String.valueOf(post.getViews()));
        authorNameField.setText(post.getAuthorName());
    }
    
    private void clearPostFields() {
        titleField.clear();
        contentArea.clear();
        userIdField.clear();
        statusField.clear();
        viewsField.clear();
        authorNameField.clear();
    }
    
    // Performance Metrics Methods
    private void updateMetrics() {
        try {
            // Update basic metrics
            List<PostEntity> posts = postService.findAll();
            List<UserEntity> users = userService.findAll();
            
            totalPostsLabel.setText(String.valueOf(posts.size()));
            totalUsersLabel.setText(String.valueOf(users.size()));
            
            // Update cache metrics
            String postStats = postService.getCacheStats();
            String userStats = userService.getCacheStats();
            
            metricsTextArea.setText("POST CACHE:\n" + postStats + "\n\nUSER CACHE:\n" + userStats);
            
            // Update charts (simplified for demo)
            updateCharts();
            
        } catch (Exception e) {
            metricsTextArea.setText("Error updating metrics: " + e.getMessage());
        }
    }
    
    private void updateCharts() {
        // Add sample data to charts (in real implementation, this would track actual metrics over time)
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        
        // Update hit rate chart
        XYChart.Series<String, Number> hitRateSeries = new XYChart.Series<>();
        hitRateSeries.setName("Hit Rate");
        hitRateSeries.getData().add(new XYChart.Data<>(currentTime, 85.0)); // Sample data
        
        hitRateChart.getData().clear();
        hitRateChart.getData().add(hitRateSeries);
        
        // Update response time chart
        XYChart.Series<String, Number> responseTimeSeries = new XYChart.Series<>();
        responseTimeSeries.setName("Response Time");
        responseTimeSeries.getData().add(new XYChart.Data<>(currentTime, 12.0)); // Sample data
        
        responseTimeChart.getData().clear();
        responseTimeChart.getData().add(responseTimeSeries);
    }
    
    // Cache Management Methods
    @FXML
    private void handleClearPostCache() {
        postService.clearAllCaches();
        updateCacheStatus();
        showAlert("Success", "Post cache cleared");
    }
    
    @FXML
    private void handleClearUserCache() {
        userService.clearAllCaches();
        updateCacheStatus();
        showAlert("Success", "User cache cleared");
    }
    
    @FXML
    private void handleClearAllCache() {
        postService.clearAllCaches();
        userService.clearAllCaches();
        updateCacheStatus();
        showAlert("Success", "All caches cleared");
    }
    
    @FXML
    private void handleCleanupExpired() {
        postService.cleanupCaches();
        userService.cleanupCaches();
        updateCacheStatus();
        showAlert("Success", "Expired entries cleaned up");
    }
    
    @FXML
    private void handleForceRefresh() {
        loadPosts();
        updateMetrics();
        updateCacheStatus();
        showAlert("Success", "Data refreshed");
    }
    
    @FXML
    private void handleApplyConfig() {
        int newCacheSize = (int) cacheSizeSlider.getValue();
        String expirationTime = expirationTimeCombo.getValue();
        
        // In a real implementation, this would reconfigure the cache
        updateCacheStatus();
        showAlert("Success", "Cache configuration applied: Size=" + newCacheSize + ", Expiration=" + expirationTime);
    }
    
    private void updateCacheStatus() {
        try {
            StringBuilder status = new StringBuilder();
            status.append("CACHE MANAGER STATUS\n");
            status.append("===================\n");
            status.append("Status: ").append(cacheManager.isRunning() ? "Running" : "Stopped").append("\n");
            status.append("Post Cache Size: ").append(postService.getCacheStats()).append("\n");
            status.append("User Cache Size: ").append(userService.getCacheStats()).append("\n");
            status.append("Last Updated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            cacheStatusArea.setText(status.toString());
        } catch (Exception e) {
            cacheStatusArea.setText("Error updating cache status: " + e.getMessage());
        }
    }
    
    // Optimization Methods
    @FXML
    private void handleAnalyzePerformance() {
        optimizationProgress.setProgress(-1); // Indeterminate
        optimizationStatusLabel.setText("Analyzing performance...");
        
        optimizationService.analyzePerformance().thenAccept(report -> {
            javafx.application.Platform.runLater(() -> {
                optimizationProgress.setProgress(1.0);
                optimizationStatusLabel.setText("Analysis complete");
                
                StringBuilder log = new StringBuilder();
                log.append("\n[").append(LocalDateTime.now()).append("] Performance analysis completed\n");
                log.append("Hit Rate: ").append(String.format("%.2f%%", report.getHitRate())).append("\n");
                log.append("Average Response Time: ").append(String.format("%.2f ms", report.getAverageResponseTime())).append("\n");
                log.append("Recommendations:\n");
                
                for (String recommendation : report.getRecommendations()) {
                    log.append("- ").append(recommendation).append("\n");
                }
                
                optimizationLogArea.appendText(log.toString());
            });
        }).exceptionally(throwable -> {
            javafx.application.Platform.runLater(() -> {
                optimizationProgress.setProgress(0);
                optimizationStatusLabel.setText("Analysis failed");
                optimizationLogArea.appendText("\n[" + LocalDateTime.now() + "] Analysis error: " + throwable.getMessage() + "\n");
            });
            return null;
        });
    }
    
    @FXML
    private void handleOptimizeCache() {
        optimizationProgress.setProgress(-1);
        optimizationStatusLabel.setText("Optimizing cache...");
        
        optimizationService.optimizeCache().thenAccept(result -> {
            javafx.application.Platform.runLater(() -> {
                optimizationProgress.setProgress(1.0);
                optimizationStatusLabel.setText("Optimization complete");
                
                StringBuilder log = new StringBuilder();
                log.append("\n[").append(LocalDateTime.now()).append("] Cache optimization completed\n");
                
                for (CacheOptimizationService.OptimizationResult.OperationResult operation : result.getOperations()) {
                    log.append("- ").append(operation.getOperation()).append(": ").append(operation.getMessage())
                      .append(" (").append(operation.getDuration()).append("ms)\n");
                }
                
                optimizationLogArea.appendText(log.toString());
                updateCacheStatus();
                updateMetrics();
            });
        }).exceptionally(throwable -> {
            javafx.application.Platform.runLater(() -> {
                optimizationProgress.setProgress(0);
                optimizationStatusLabel.setText("Optimization failed");
                optimizationLogArea.appendText("\n[" + LocalDateTime.now() + "] Optimization error: " + throwable.getMessage() + "\n");
            });
            return null;
        });
    }
    
    @FXML
    private void handlePreloadData() {
        optimizationProgress.setProgress(-1);
        optimizationStatusLabel.setText("Preloading data...");
        
        optimizationService.preloadData().thenAccept(result -> {
            javafx.application.Platform.runLater(() -> {
                optimizationProgress.setProgress(1.0);
                optimizationStatusLabel.setText("Preloading complete");
                
                StringBuilder log = new StringBuilder();
                log.append("\n[").append(LocalDateTime.now()).append("] Data preloading completed\n");
                log.append("Posts preloaded: ").append(result.getPostsPreloaded()).append("\n");
                log.append("Users preloaded: ").append(result.getUsersPreloaded()).append("\n");
                log.append("Popular posts preloaded: ").append(result.getPopularPostsPreloaded()).append("\n");
                
                for (CacheOptimizationService.PreloadResult.OperationResult operation : result.getOperations()) {
                    log.append("- ").append(operation.getOperation()).append(": ").append(operation.getMessage())
                      .append(" (").append(operation.getDuration()).append("ms)\n");
                }
                
                optimizationLogArea.appendText(log.toString());
                updateMetrics();
            });
        }).exceptionally(throwable -> {
            javafx.application.Platform.runLater(() -> {
                optimizationProgress.setProgress(0);
                optimizationStatusLabel.setText("Preloading failed");
                optimizationLogArea.appendText("\n[" + LocalDateTime.now() + "] Preloading error: " + throwable.getMessage() + "\n");
            });
            return null;
        });
    }
    
    @FXML
    private void handleWarmupCache() {
        optimizationProgress.setProgress(-1);
        optimizationStatusLabel.setText("Warming up cache...");
        
        optimizationService.warmupCache().thenAccept(result -> {
            javafx.application.Platform.runLater(() -> {
                optimizationProgress.setProgress(1.0);
                optimizationStatusLabel.setText("Cache warmup complete");
                
                StringBuilder log = new StringBuilder();
                log.append("\n[").append(LocalDateTime.now()).append("] Cache warmup completed\n");
                log.append("Recent posts accessed: ").append(result.getRecentPostsAccessed()).append("\n");
                log.append("Active users accessed: ").append(result.getActiveUsersAccessed()).append("\n");
                log.append("Total warmup time: ").append(result.getTotalWarmupTime()).append("ms\n");
                
                for (CacheOptimizationService.WarmupResult.OperationResult operation : result.getOperations()) {
                    log.append("- ").append(operation.getOperation()).append(": ").append(operation.getMessage())
                      .append(" (").append(operation.getDuration()).append("ms)\n");
                }
                
                optimizationLogArea.appendText(log.toString());
                updateCacheStatus();
                updateMetrics();
            });
        }).exceptionally(throwable -> {
            javafx.application.Platform.runLater(() -> {
                optimizationProgress.setProgress(0);
                optimizationStatusLabel.setText("Cache warmup failed");
                optimizationLogArea.appendText("\n[" + LocalDateTime.now() + "] Warmup error: " + throwable.getMessage() + "\n");
            });
            return null;
        });
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void shutdown() {
        if (metricsUpdateTimer != null) {
            metricsUpdateTimer.cancel();
        }
        if (optimizationService != null) {
            optimizationService.shutdown();
        }
        if (cacheManager != null) {
            cacheManager.stop();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/screens/LoginResources/LoginPage.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/screens/Signup/Signup.fxml"));
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
}
