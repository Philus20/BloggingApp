package org.example.bloggingapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Models.CommentEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 🧩 Comment Controller - Dedicated Comment Scene Controller
 * 
 * Features:
 * - Modal comment scene that can be toggled
 * - Load comments for specific posts
 * - Add new comments
 * - Close/hide functionality
 */
public class CommentController {
    
    // ==================== FXML COMPONENTS ===================
    
    // Header Components
    @FXML private Label commentTitle;
    @FXML private Button closeButton;
    
    // Post Context Components
    @FXML private VBox postContext;
    @FXML private Circle postAuthorAvatar;
    @FXML private Label postAuthorLabel;
    @FXML private Label postTimeLabel;
    @FXML private Label postContentLabel;
    
    // Comments Components
    @FXML private ScrollPane commentsScrollPane;
    @FXML private VBox commentsContainer;
    
    // Add Comment Components
    @FXML private Circle currentUserAvatar;
    @FXML private TextArea commentField;
    @FXML private Label charCountLabel;
    @FXML private Button submitButton;
    
    // ==================== DATA LAYER ===================
    
    private PostEntity currentPost;
    private Stage commentStage;
    private boolean isVisible = false;
    private Map<Integer, List<CommentEntity>> postComments = new HashMap<>();
    
    // ==================== INITIALIZATION ===================
    
    @FXML
    public void initialize() {
        System.out.println("🚀 Initializing CommentController");
        
        // Setup event handlers
        setupEventHandlers();
        
        // Initialize with empty state
        clearComments();
        
        System.out.println("✅ CommentController initialized successfully");
    }
    
    private void setupEventHandlers() {
        // Character counter for comment field
        commentField.textProperty().addListener((obs, oldVal, newVal) -> {
            int length = newVal.length();
            charCountLabel.setText(length + "/500");
            
            // Change color if approaching limit
            if (length > 450) {
                charCountLabel.setStyle("-fx-text-fill: #dc3545;");
            } else if (length > 400) {
                charCountLabel.setStyle("-fx-text-fill: #ffc107;");
            } else {
                charCountLabel.setStyle("-fx-text-fill: #6c757d;");
            }
        });
        
        // Submit on Ctrl+Enter
        commentField.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode().toString().equals("ENTER")) {
                submitComment(null);
            }
        });
    }
    
    // ==================== PUBLIC METHODS ===================
    
    /**
     * Shows the comment scene for a specific post
     */
    public void showCommentScene(PostEntity post) {
        try {
            this.currentPost = post;
            
            if (commentStage == null) {
                // Load the comment scene
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bloggingapp/fxml/comment_scene.fxml"));
                loader.setController(this);
                Parent root = loader.load();
                
                // Create and configure stage
                commentStage = new Stage();
                commentStage.setTitle("Comments - Post #" + post.getPostId());
                commentStage.setScene(new Scene(root));
                commentStage.initModality(Modality.NONE);
                commentStage.initStyle(StageStyle.TRANSPARENT);
                commentStage.setResizable(true);
                commentStage.setMinWidth(400);
                commentStage.setMinHeight(500);
                
                // Handle close request
                commentStage.setOnCloseRequest(event -> {
                    isVisible = false;
                    commentStage.hide();
                });
            }
            
            // Load post data and comments
            loadPostContext(post);
            loadComments(post.getPostId());
            
            // Show the stage
            if (!isVisible) {
                commentStage.show();
                isVisible = true;
                System.out.println("👁️ Comment scene shown for post: " + post.getPostId());
            } else {
                commentStage.toFront();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error showing comment scene: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Toggles the comment scene visibility
     */
    public void toggleCommentScene(PostEntity post) {
        if (isVisible && currentPost != null && currentPost.getPostId() == post.getPostId()) {
            hideCommentScene();
        } else {
            showCommentScene(post);
        }
    }
    
    /**
     * Hides the comment scene
     */
    public void hideCommentScene() {
        if (commentStage != null && isVisible) {
            commentStage.hide();
            isVisible = false;
            System.out.println("👁️ Comment scene hidden");
        }
    }
    
    /**
     * Closes the comment scene (called by close button)
     */
    @FXML
    private void closeCommentScene(ActionEvent event) {
        hideCommentScene();
    }
    
    // ==================== PRIVATE METHODS ===================
    
    /**
     * Loads post context information
     */
    private void loadPostContext(PostEntity post) {
        postAuthorLabel.setText(post.getAuthorName());
        postTimeLabel.setText(formatTime(post.getCreatedAt()));
        postContentLabel.setText(post.getContent());
        commentTitle.setText("Comments - " + post.getAuthorName() + "'s Post");
    }
    
    /**
     * Loads comments for a specific post
     */
    private void loadComments(int postId) {
        commentsContainer.getChildren().clear();
        
        // Get comments for this post (or create empty list)
        List<CommentEntity> comments = postComments.getOrDefault(postId, new ArrayList<>());
        
        if (comments.isEmpty()) {
            // Show no comments message
            Label noCommentsLabel = new Label("No comments yet. Be the first to comment!");
            noCommentsLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic; -fx-padding: 20;");
            commentsContainer.getChildren().add(noCommentsLabel);
        } else {
            // Load each comment
            for (CommentEntity comment : comments) {
                addCommentToUI(comment);
            }
        }
    }
    
    /**
     * Adds a comment to the UI
     */
    private void addCommentToUI(CommentEntity comment) {
        HBox commentBox = new HBox(12);
        commentBox.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 8;");
        
        // Avatar
        Circle avatar = new Circle(15);
        avatar.setStyle("-fx-fill: #007bff;");
        
        // Comment content
        VBox commentContent = new VBox(5);
        commentContent.setStyle("-fx-background-color: transparent;");
        HBox.setHgrow(commentContent, Priority.ALWAYS);
        
        // Author and time
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label authorLabel = new Label("User " + comment.getUserId());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057;");
        
        Label timeLabel = new Label(formatTime(comment.getCreatedAt()));
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        headerBox.getChildren().addAll(authorLabel, timeLabel);
        
        // Comment text
        Label commentLabel = new Label(comment.getContent());
        commentLabel.setStyle("-fx-text-fill: #212529; -fx-wrap-text: true;");
        commentLabel.setPrefWidth(300);
        
        commentContent.getChildren().addAll(headerBox, commentLabel);
        commentBox.getChildren().addAll(avatar, commentContent);
        
        commentsContainer.getChildren().add(commentBox);
    }
    
    /**
     * Submits a new comment
     */
    @FXML
    private void submitComment(ActionEvent event) {
        String content = commentField.getText().trim();
        
        if (content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Comment", "Please write a comment before submitting.");
            return;
        }
        
        if (content.length() > 500) {
            showAlert(Alert.AlertType.WARNING, "Comment Too Long", "Comment must be 500 characters or less.");
            return;
        }
        
        try {
            // Create new comment
            CommentEntity comment = new CommentEntity();
            comment.setPostId(currentPost.getPostId());
            comment.setUserId(1); // This would come from user session
            comment.setContent(content);
            comment.setCreatedAt(LocalDateTime.now());
            
            // Add to data structure
            List<CommentEntity> comments = postComments.computeIfAbsent(currentPost.getPostId(), k -> new ArrayList<>());
            comments.add(comment);
            
            // Add to UI
            addCommentToUI(comment);
            
            // Clear comment field
            commentField.clear();
            
            // Scroll to bottom
            commentsScrollPane.setVvalue(1.0);
            
            System.out.println("✅ Comment added successfully for post: " + currentPost.getPostId());
            
        } catch (Exception e) {
            System.err.println("❌ Error submitting comment: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit comment. Please try again.");
        }
    }
    
    /**
     * Clears all comments from the UI
     */
    private void clearComments() {
        commentsContainer.getChildren().clear();
    }
    
    /**
     * Formats time for display
     */
    private String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown time";
        
        LocalDateTime now = LocalDateTime.now();
        long hoursDiff = java.time.Duration.between(dateTime, now).toHours();
        
        if (hoursDiff < 1) {
            long minutesDiff = java.time.Duration.between(dateTime, now).toMinutes();
            return minutesDiff + " minutes ago";
        } else if (hoursDiff < 24) {
            return hoursDiff + " hours ago";
        } else if (hoursDiff < 48) {
            return "Yesterday";
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        }
    }
    
    /**
     * Shows an alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ==================== UTILITY METHODS ===================
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public PostEntity getCurrentPost() {
        return currentPost;
    }
}
