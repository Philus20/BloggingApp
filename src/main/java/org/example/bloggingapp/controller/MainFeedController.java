package org.example.bloggingapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Models.CommentEntity;
import org.example.bloggingapp.Models.ReviewEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 🧩 Main Feed Controller - Modern Social Media Style Blogging Interface
 * 
 * Features:
 * - Post creation with hashtag support
 * - Dynamic feed loading
 * - Comment and review functionality
 * - Search and filtering
 * - Clean service layer separation
 */
public class MainFeedController {
    
    // ==================== FXML COMPONENTS ===================
    
    // Header Components
    @FXML private TextField searchField;
    @FXML private Button refreshButton;
    
    // Create Post Components
    @FXML private TextArea postContentField;
    @FXML private Label charCountLabel;
    @FXML private Button postButton;
    
    // Feed Components
    @FXML private ScrollPane feedScrollPane;
    @FXML private VBox feedContainer;
    
    // Comment Components
    @FXML private VBox commentSection;
    @FXML private TextArea commentField;
    @FXML private Button submitCommentButton;
    
    // Review Components
    @FXML private VBox reviewSection;
    @FXML private Label star1, star2, star3, star4, star5;
    @FXML private TextArea reviewCommentField;
    @FXML private Button submitReviewButton;
    
    // ==================== DATA LAYER ===================
    
    private List<PostEntity> allPosts;
    private List<PostEntity> filteredPosts;
    private PostEntity currentPostForComment;
    private PostEntity currentPostForReview;
    private int selectedRating = 0;
    
    // ==================== SERVICE LAYER ===================
    // These would be injected via dependency injection in a real app
    // private PostService postService;
    // private CommentService commentService;
    // private TagService tagService;
    // private ReviewService reviewService;
    
    // ==================== INITIALIZATION ===================
    
    @FXML
    public void initialize() {
        System.out.println("🚀 Initializing MainFeedController");
        
        // Initialize data structures
        allPosts = new ArrayList<>();
        filteredPosts = new ArrayList<>();
        
        // Setup event handlers
        setupEventHandlers();
        
        // Load initial posts
        loadPosts();
        
        System.out.println("✅ MainFeedController initialized successfully");
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchPosts(newVal));
        
        // Character counter for post content
        postContentField.textProperty().addListener((obs, oldVal, newVal) -> {
            int length = newVal != null ? newVal.length() : 0;
            charCountLabel.setText(length + "/280");
            charCountLabel.setStyle(length > 280 ? "-fx-text-fill: #ff4444;" : "-fx-text-fill: #666666;");
        });
        
        // Setup star rating handlers
        setupStarRatingHandlers();
    }
    
    private void setupStarRatingHandlers() {
        Label[] stars = {star1, star2, star3, star4, star5};
        
        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i + 1;
            stars[i].setOnMouseClicked(e -> {
                selectedRating = starIndex;
                updateStarDisplay(starIndex);
                System.out.println("⭐ Selected rating: " + starIndex);
            });
            
            // Hover effects
            stars[i].setOnMouseEntered(e -> updateStarHover(starIndex, true));
            stars[i].setOnMouseExited(e -> updateStarHover(starIndex, false));
        }
    }
    
    private void updateStarDisplay(int rating) {
        Label[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setText("⭐");
                stars[i].setStyle("-fx-font-size: 24px; -fx-cursor: hand;");
            } else {
                stars[i].setText("☆");
                stars[i].setStyle("-fx-font-size: 24px; -fx-cursor: hand; -fx-opacity: 0.5;");
            }
        }
    }
    
    private void updateStarHover(int rating, boolean isHovering) {
        if (isHovering && rating <= selectedRating) return;
        
        Label[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setText(isHovering ? "⭐" : "☆");
                stars[i].setStyle("-fx-font-size: 24px; -fx-cursor: hand;");
            } else {
                stars[i].setText("☆");
                stars[i].setStyle("-fx-font-size: 24px; -fx-cursor: hand; -fx-opacity: 0.5;");
            }
        }
    }
    
    // ==================== CORE METHODS ===================
    
    /**
     * 📝 Load posts from service layer
     */
    private void loadPosts() {
        try {
            // In real implementation: allPosts = postService.getAllPosts();
            // For demo: Create sample posts
            createSamplePosts();
            
            filteredPosts = new ArrayList<>(allPosts);
            refreshFeed();
            
            System.out.println("📋 Loaded " + allPosts.size() + " posts");
        } catch (Exception e) {
            System.err.println("❌ Error loading posts: " + e.getMessage());
            showAlert("Error", "Failed to load posts. Please try again.");
        }
    }
    
    /**
     * 📝 Create new post with hashtag extraction
     */
    @FXML
    private void createPost(ActionEvent event) {
        String content = postContentField.getText().trim();
        
        if (content.isEmpty()) {
            showAlert("Error", "Please write something before posting!");
            return;
        }
        
        if (content.length() > 280) {
            showAlert("Error", "Post is too long! Maximum 280 characters.");
            return;
        }
        
        try {
            // Create new post
            PostEntity newPost = new PostEntity(
                generatePostId(),
                extractTitleFromContent(content),
                content,
                LocalDateTime.now(),
                1, // Current user ID (would come from session)
                "Published",
                0,
                "Current User"
            );
            
            // In real implementation: postService.createPost(newPost);
            allPosts.add(0, newPost); // Add to beginning of list
            
            // Extract and save hashtags
            List<String> hashtags = extractAndSaveTags(content);
            System.out.println("🏷️ Extracted hashtags: " + hashtags);
            
            // Clear form
            postContentField.clear();
            charCountLabel.setText("0/280");
            
            // Refresh feed
            filteredPosts = new ArrayList<>(allPosts);
            refreshFeed();
            
            showAlert("Success", "Post published successfully!");
            System.out.println("✅ Created new post: " + newPost.getTitle());
            
        } catch (Exception e) {
            System.err.println("❌ Error creating post: " + e.getMessage());
            showAlert("Error", "Failed to create post. Please try again.");
        }
    }
    
    /**
     * 🏷️ Extract hashtags from content and save via service
     */
    private List<String> extractAndSaveTags(String content) {
        List<String> hashtags = new ArrayList<>();
        
        // Find all #hashtags
        String[] words = content.split("\\s+");
        for (String word : words) {
            if (word.startsWith("#") && word.length() > 1) {
                String hashtag = word.substring(1).toLowerCase();
                if (!hashtags.contains(hashtag)) {
                    hashtags.add(hashtag);
                    
                    // In real implementation: tagService.createTag(hashtag);
                    // In real implementation: postTagService.linkPostToTag(postId, tagId);
                }
            }
        }
        
        return hashtags;
    }
    
    /**
     * 🔍 Search posts by content, title, or tags
     */
    private void searchPosts(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredPosts = new ArrayList<>(allPosts);
        } else {
            String searchQuery = query.toLowerCase().trim();
            filteredPosts = allPosts.stream()
                .filter(post -> 
                    post.getTitle().toLowerCase().contains(searchQuery) ||
                    post.getContent().toLowerCase().contains(searchQuery) ||
                    post.getContent().contains("#" + searchQuery))
                .toList();
        }
        
        refreshFeed();
        System.out.println("🔍 Search results: " + filteredPosts.size() + " posts for '" + query + "'");
    }
    
    /**
     * 💬 Add comment to a post
     */
    @FXML
    private void addComment(ActionEvent event) {
        if (currentPostForComment == null) {
            showAlert("Error", "Please select a post to comment on");
            return;
        }
        
        String commentText = commentField.getText().trim();
        if (commentText.isEmpty()) {
            showAlert("Error", "Please write a comment");
            return;
        }
        
        try {
            // Create new comment
            CommentEntity newComment = new CommentEntity(
                generateCommentId(),
                commentText,
                LocalDateTime.now(),
                currentPostForComment.getPostId(),
                1 // Current user ID
            );
            
            // In real implementation: commentService.addComment(newComment);
            System.out.println("💬 Created comment: " + newComment.getContent());
            
            commentField.clear();
            hideCommentSection();
            
            showAlert("Success", "Comment added successfully!");
            System.out.println("💬 Added comment to post " + currentPostForComment.getPostId());
            
        } catch (Exception e) {
            System.err.println("❌ Error adding comment: " + e.getMessage());
            showAlert("Error", "Failed to add comment. Please try again.");
        }
    }
    
    /**
     * ⭐ Submit review for a post
     */
    @FXML
    private void submitReview(ActionEvent event) {
        if (currentPostForReview == null) {
            showAlert("Error", "Please select a post to review");
            return;
        }
        
        if (selectedRating == 0) {
            showAlert("Error", "Please select a star rating");
            return;
        }
        
        String reviewText = reviewCommentField.getText().trim();
        if (reviewText.isEmpty()) {
            showAlert("Error", "Please write a review comment");
            return;
        }
        
        try {
            // Create new review
            ReviewEntity newReview = new ReviewEntity(
                generateReviewId(),
                selectedRating,
                reviewText,
                1, // Current user ID
                currentPostForReview.getPostId()
            );
            
            // In real implementation: reviewService.addReview(newReview);
            System.out.println("⭐ Added " + selectedRating + "-star review to post " + currentPostForReview.getPostId());
            
            // Reset form
            reviewCommentField.clear();
            selectedRating = 0;
            updateStarDisplay(0);
            hideReviewSection();
            
            showAlert("Success", "Review submitted successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error submitting review: " + e.getMessage());
            showAlert("Error", "Failed to submit review. Please try again.");
        }
    }
    
    /**
     * 🔄 Refresh posts from service
     */
    @FXML
    private void refreshPosts(ActionEvent event) {
        loadPosts();
        showAlert("Success", "Posts refreshed!");
    }
    
    // ==================== UI HELPER METHODS ===================
    
    private void refreshFeed() {
        feedContainer.getChildren().clear();
        
        if (filteredPosts.isEmpty()) {
            Label noPostsLabel = new Label("📭 No posts found");
            noPostsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
            feedContainer.getChildren().add(noPostsLabel);
            return;
        }
        
        for (PostEntity post : filteredPosts) {
            VBox postCard = createPostCard(post);
            feedContainer.getChildren().add(postCard);
        }
    }
    
    private VBox createPostCard(PostEntity post) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 15; " +
                   "-fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-border-width: 1;");
        
        // Post header
        Label authorLabel = new Label(post.getAuthorName());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1d9bf0;");
        
        Label timeLabel = new Label(formatTimeAgo(post.getCreatedAt()));
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        
        // Post content
        Label contentLabel = new Label(post.getContent());
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-wrap-text: true;");
        contentLabel.setPrefWidth(400);
        
        // Post actions
        HBox actionsBox = new HBox(10);
        actionsBox.setStyle("-fx-alignment: center-left;");
        
        Button commentBtn = new Button("💬 " + getCommentCount(post));
        commentBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #1d9bf0; " +
                        "-fx-border-radius: 20; -fx-text-fill: #1d9bf0; -fx-cursor: hand;");
        commentBtn.setOnAction(e -> showCommentSection(post));
        
        Button reviewBtn = new Button("⭐ Review");
        reviewBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #ffd700; " +
                       "-fx-border-radius: 20; -fx-text-fill: #ffd700; -fx-cursor: hand;");
        reviewBtn.setOnAction(e -> showReviewSection(post));
        
        Button loadCommentsBtn = new Button("📂 Load Comments");
        loadCommentsBtn.setStyle("-fx-background-color: #f0f0f0; -fx-border-radius: 20; " +
                            "-fx-text-fill: #333333; -fx-cursor: hand;");
        loadCommentsBtn.setOnAction(e -> loadCommentsForPost(post));
        
        actionsBox.getChildren().addAll(commentBtn, reviewBtn, loadCommentsBtn);
        
        card.getChildren().addAll(authorLabel, timeLabel, contentLabel, actionsBox);
        return card;
    }
    
    private void showCommentSection(PostEntity post) {
        currentPostForComment = post;
        commentSection.setVisible(true);
        commentSection.setManaged(true);
        commentField.requestFocus();
    }
    
    private void hideCommentSection() {
        commentSection.setVisible(false);
        commentSection.setManaged(false);
        currentPostForComment = null;
    }
    
    private void showReviewSection(PostEntity post) {
        currentPostForReview = post;
        reviewSection.setVisible(true);
        reviewSection.setManaged(true);
        reviewCommentField.requestFocus();
    }
    
    private void hideReviewSection() {
        reviewSection.setVisible(false);
        reviewSection.setManaged(false);
        currentPostForReview = null;
    }
    
    private void loadCommentsForPost(PostEntity post) {
        // In real implementation: List<CommentEntity> comments = commentService.getCommentsByPostId(post.getPostId());
        showAlert("Info", "Loaded " + getCommentCount(post) + " comments for post " + post.getPostId());
        System.out.println("📂 Loading comments for post: " + post.getPostId());
    }
    
    // ==================== UTILITY METHODS ===================
    
    private void createSamplePosts() {
        allPosts.clear();
        
        // Sample posts with hashtags
        allPosts.add(new PostEntity(1, "🚀 Just launched our new blogging platform!", 
            "Check out this amazing #JavaFX #SocialMedia platform we built. Features modern UI with #hashtags support! #WebDev", 
            LocalDateTime.now().minusHours(1), 1, "Published", 42, "Dev Team"));
        
        allPosts.add(new PostEntity(2, "🎨 Working on UI improvements", 
            "Adding dark mode and better #responsive design. What features would you like to see? #Feedback", 
            LocalDateTime.now().minusHours(3), 2, "Published", 28, "Sarah Chen"));
        
        allPosts.add(new PostEntity(3, "📚 Database optimization complete!", 
            "Improved query performance by 300%. Now using #PostgreSQL with proper indexing. #Database #Performance", 
            LocalDateTime.now().minusHours(6), 1, "Published", 156, "Mike Johnson"));
        
        allPosts.add(new PostEntity(4, "🔥 Trending: Modern Web Development", 
            "The latest trends in #JavaScript #React #Vue #Angular frameworks are exciting! #Frontend", 
            LocalDateTime.now().minusHours(12), 3, "Published", 89, "Emily Davis"));
        
        allPosts.add(new PostEntity(5, "💡 Pro tip: Clean code matters", 
            "Always write clean, maintainable code. Use proper naming conventions and #DesignPatterns. #Programming", 
            LocalDateTime.now().minusDays(1), 2, "Published", 234, "Alex Kumar"));
    }
    
    private String extractTitleFromContent(String content) {
        String[] words = content.split("\\s+");
        for (String word : words) {
            if (!word.startsWith("#") && word.length() > 3) {
                return word.length() > 50 ? word.substring(0, 47) + "..." : word;
            }
        }
        return "New Post";
    }
    
    private String formatTimeAgo(LocalDateTime dateTime) {
        long hours = java.time.Duration.between(dateTime, LocalDateTime.now()).toHours();
        if (hours < 1) return "just now";
        if (hours < 24) return hours + "h ago";
        long days = hours / 24;
        return days + "d ago";
    }
    
    private int getCommentCount(PostEntity post) {
        // In real implementation: return commentService.getCommentCountByPostId(post.getPostId());
        return (int) (Math.random() * 20); // Mock data
    }
    
    // ==================== ID GENERATORS ===================
    
    private int generatePostId() {
        return allPosts.size() + 1;
    }
    
    private int generateCommentId() {
        return (int) (Math.random() * 10000);
    }
    
    private int generateReviewId() {
        return (int) (Math.random() * 10000);
    }
    
    // ==================== NAVIGATION ===================
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
