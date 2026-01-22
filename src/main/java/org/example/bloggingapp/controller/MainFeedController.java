package org.example.bloggingapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Models.CommentEntity;
import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Models.ReviewEntity;
import org.example.bloggingapp.Models.TagEntity;
import org.example.bloggingapp.Models.PostTagEntity;
import org.example.bloggingapp.Database.factories.ServiceFactory;
import org.example.bloggingapp.Database.Services.PostService;
import org.example.bloggingapp.Database.Services.CommentService;
import org.example.bloggingapp.Database.Services.UserService;
import org.example.bloggingapp.Database.Services.ReviewService;
import org.example.bloggingapp.Database.Services.TagService;
import org.example.bloggingapp.Database.Services.PostTagService;
import org.example.bloggingapp.Database.Utils.RegexPatterns;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
    @FXML private ScrollPane commentsScrollPane;
    @FXML private VBox commentsContainer;
    
    // Review Components
    @FXML private VBox reviewSection;
    @FXML private Label star1, star2, star3, star4, star5;
    @FXML private TextArea reviewCommentField;
    @FXML private Button submitReviewButton;
    
    // ==================== SERVICE LAYER ===================
    
    private ServiceFactory serviceFactory;
    private PostService postService;
    private CommentService commentService;
    private UserService userService;
    private ReviewService reviewService;
    private TagService tagService;
    private PostTagService postTagService;
    
    // ==================== DATA LAYER ===================
    
    private List<PostEntity> allPosts;
    private List<PostEntity> filteredPosts;
    private PostEntity currentPostForComment;
    private PostEntity currentPostForReview;
    private int selectedRating = 0;
    private int currentUserId = 1; // This would come from user session
    
    // Comment data
    private Map<Integer, List<CommentEntity>> postComments = new HashMap<>();
    private Map<Integer, VBox> commentUIComponents = new HashMap<>();
    
    // ==================== USER SESSION MANAGEMENT ===================
    
    /**
     * Sets the current user ID from login session
     */
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        System.out.println("👤 Set current user ID: " + userId);
        
        // Reload posts to ensure user-specific data
        loadPosts();
    }
    
    /**
     * Gets the current user ID
     */
    public int getCurrentUserId() {
        return currentUserId;
    }
    
    // ==================== INITIALIZATION ===================
    
    @FXML
    public void initialize() {
        System.out.println("🚀 Initializing MainFeedController");
        
        try {
            // Initialize services using ServiceFactory
            this.serviceFactory = ServiceFactory.getInstance();
            this.postService = serviceFactory.getPostService();
            this.commentService = serviceFactory.getCommentService();
            this.userService = serviceFactory.getUserService();
            this.reviewService = serviceFactory.getReviewService();
            this.tagService = serviceFactory.getTagService();
            this.postTagService = serviceFactory.getPostTagService();
            
            // Initialize data structures
            allPosts = new ArrayList<>();
            filteredPosts = new ArrayList<>();
            
            // Setup event handlers
            setupEventHandlers();
            
            // Load initial posts from database
            loadPosts();
            
            System.out.println("✅ MainFeedController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize MainFeedController: " + e.getMessage());
            showAlert("Initialization Error", "Failed to load application data. Please restart the application.");
        }
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
            // Load posts from database using service layer
            allPosts = postService.findAll();
            
            // Update author names using user service
            for (PostEntity post : allPosts) {
                if (post.getUserId() > 0) {
                    UserEntity user = userService.findById(post.getUserId());
                    if (user != null) {
                        post.setAuthorName(user.getUserName());
                    } else {
                        post.setAuthorName("Unknown User");
                    }
                } else {
                    post.setAuthorName("Anonymous");
                }
            }
            
            filteredPosts = new ArrayList<>(allPosts);
            refreshFeed();
            
            System.out.println("📋 Loaded " + allPosts.size() + " posts from database");
        } catch (Exception e) {
            System.err.println("❌ Error loading posts: " + e.getMessage());
            showAlert("Database Error", "Failed to load posts from database. Please check your connection.");
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
        
        // Content validation using RegexPatterns
        if (!RegexPatterns.isLengthValid(content, 1, 280)) {
            showAlert("Error", "Post content must be between 1 and 280 characters.");
            return;
        }
        
        try {
            // Create new post entity
            PostEntity newPost = new PostEntity();
            newPost.setTitle(extractTitleFromContent(content));
            newPost.setContent(content);
            newPost.setCreatedAt(LocalDateTime.now());
            newPost.setUserId(currentUserId);
            newPost.setStatus("Published");
            newPost.setViews(0);
            
            // Get current user info for author name
            UserEntity currentUser = userService.findById(currentUserId);
            if (currentUser != null) {
                newPost.setAuthorName(currentUser.getUserName());
            } else {
                newPost.setAuthorName("Current User");
            }
            
            // Save post to database using service layer
            PostEntity createdPost = postService.create(newPost);
            
            // Extract and save hashtags
            List<String> extractedTags = extractAndSaveTags(content, createdPost.getPostId());
            System.out.println("🏷️ Extracted and saved " + extractedTags.size() + " tags: " + extractedTags);
            
            // Add to local list
            allPosts.add(0, createdPost);
            
            // Clear form
            postContentField.clear();
            charCountLabel.setText("0/280");
            
            // Refresh feed
            filteredPosts = new ArrayList<>(allPosts);
            refreshFeed();
            
            showAlert("Success", "Post published successfully!");
            System.out.println("✅ Created new post with ID: " + createdPost.getPostId());
            
        } catch (Exception e) {
            System.err.println("❌ Error creating post: " + e.getMessage());
            showAlert("Error", "Failed to create post. Please try again.");
        }
    }
    
    /**
     * 🏷️ Extract hashtags from content and save via service
     */
    private List<String> extractAndSaveTags(String content, int postId) {
        List<String> hashtags = new ArrayList<>();
        
        // Find all #hashtags
        String[] words = content.split("\\s+");
        for (String word : words) {
            if (word.startsWith("#") && word.length() > 1) {
                String hashtag = word.substring(1).toLowerCase();
                if (!hashtags.contains(hashtag)) {
                    hashtags.add(hashtag);
                    
                    try {
                        // Create or find the tag
                        TagEntity existingTag = tagService.findByName(hashtag);
                        TagEntity tag;
                        
                        if (existingTag == null) {
                            // Create new tag
                            tag = new TagEntity();
                            tag.setName(hashtag);
                            tag = tagService.create(tag);
                            System.out.println("🏷️ Created new tag: " + hashtag + " with ID: " + tag.getTagId());
                        } else {
                            tag = existingTag;
                            System.out.println("🏷️ Using existing tag: " + hashtag + " with ID: " + tag.getTagId());
                        }
                        
                        // Link post to tag
                        PostTagEntity postTag = new PostTagEntity();
                        postTag.setPostId(postId);
                        postTag.setTagId(tag.getTagId());
                        postTagService.create(postTag);
                        System.out.println("🔗 Linked post " + postId + " to tag " + hashtag);
                        
                    } catch (Exception e) {
                        System.err.println("❌ Error saving tag " + hashtag + ": " + e.getMessage());
                        // Continue with other tags even if one fails
                    }
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
            
            // Add comment to storage
            List<CommentEntity> comments = postComments.computeIfAbsent(currentPostForComment.getPostId(), k -> new ArrayList<>());
            comments.add(0, newComment); // Add to beginning
            
            // Add comment to display
            VBox commentCard = createCommentCard(newComment);
            commentsContainer.getChildren().add(0, commentCard);
            
            // Clear form
            commentField.clear();
            
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
            // Create new review entity
            ReviewEntity newReview = new ReviewEntity();
            newReview.setRating(selectedRating);
            newReview.setComment(reviewText);
            newReview.setUserId(currentUserId);
            newReview.setPostId(currentPostForReview.getPostId());
            newReview.setCreatedAt(LocalDateTime.now());
            
            // Save review to database using service layer
            ReviewEntity createdReview = reviewService.create(newReview);
            System.out.println("⭐ Added " + selectedRating + "-star review to post " + currentPostForReview.getPostId() + " with review ID: " + createdReview.getReviewId());
            
            // Reset form
            reviewCommentField.clear();
            selectedRating = 0;
            updateStarDisplay(0);
            hideReviewSection();
            
            showAlert("Success", "Review submitted successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error submitting review: " + e.getMessage());
            e.printStackTrace();
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
        VBox contentBox = new VBox(5);
        contentBox.setPrefWidth(400);
        
        // Parse content and make hashtags clickable
        String[] contentParts = post.getContent().split("(?=#)");
        for (String part : contentParts) {
            if (part.startsWith("#")) {
                // Extract hashtag
                String[] hashtagParts = part.split("\\s+", 2);
                String hashtag = hashtagParts[0].substring(1); // Remove #
                String remainingText = hashtagParts.length > 1 ? hashtagParts[1] : "";
                
                // Create clickable hashtag
                HBox hashtagBox = new HBox(2);
                Label hashtagLabel = new Label("#" + hashtag);
                hashtagLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1d9bf0; -fx-cursor: hand;");
                hashtagLabel.setOnMouseClicked(e -> searchByHashtag(hashtag));
                
                hashtagBox.getChildren().add(hashtagLabel);
                if (!remainingText.isEmpty()) {
                    Label remainingLabel = new Label(remainingText);
                    remainingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-wrap-text: true;");
                    hashtagBox.getChildren().add(remainingLabel);
                }
                
                contentBox.getChildren().add(hashtagBox);
            } else {
                // Regular text
                Label textLabel = new Label(part);
                textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-wrap-text: true;");
                textLabel.setPrefWidth(400);
                contentBox.getChildren().add(textLabel);
            }
        }
        
        // Post actions
        HBox actionsBox = new HBox(10);
        actionsBox.setStyle("-fx-alignment: center-left;");
        
        Button commentBtn = new Button("💬 " + getCommentCount(post));
        commentBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #1d9bf0; " +
                        "-fx-border-radius: 20; -fx-text-fill: #1d9bf0; -fx-cursor: hand;");
        commentBtn.setOnAction(e -> {
            System.out.println("🔍 Comment button clicked for post: " + post.getPostId());
            toggleInlineComments(post, card);
        });
        
        Button reviewBtn = new Button("⭐ Review");
        reviewBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #ffd700; " +
                       "-fx-border-radius: 20; -fx-text-fill: #ffd700; -fx-cursor: hand;");
        reviewBtn.setOnAction(e -> toggleReviewSection(post));
        
        actionsBox.getChildren().addAll(commentBtn, reviewBtn);
        
        // Inline comments section (initially hidden)
        VBox inlineCommentsSection = createInlineCommentsSection(post);
        inlineCommentsSection.setVisible(false);
        inlineCommentsSection.setManaged(false);
        
        // Store reference to comments section for toggling
        commentUIComponents.put(post.getPostId(), inlineCommentsSection);
        
        card.getChildren().addAll(authorLabel, timeLabel, contentBox, actionsBox, inlineCommentsSection);
        return card;
    }
    
    /**
     * 🔍 Search posts by hashtag
     */
    private void searchByHashtag(String hashtag) {
        System.out.println("🔍 Searching for hashtag: #" + hashtag);
        searchField.setText("#" + hashtag);
        searchPosts("#" + hashtag);
    }
    
    private void showCommentSection(PostEntity post) {
        System.out.println("🔍 Showing comment section for post: " + post.getPostId());
        currentPostForComment = post;
        commentSection.setVisible(true);
        commentSection.setManaged(true);
        commentField.requestFocus();
        
        // Load comments for this post automatically
        loadCommentsForPost(post);
        
        System.out.println("✅ Comment section should now be visible");
    }
    
    private void hideCommentSection() {
        commentSection.setVisible(false);
        commentSection.setManaged(false);
        currentPostForComment = null;
    }
    
    private void toggleCommentSection(PostEntity post) {
        System.out.println("🔄 Toggling comment section for post: " + post.getPostId());
        
        // Use inline comments instead of modal
        System.out.println("🔍 Using inline comment system for post: " + post.getPostId());
    }
    
    private void toggleReviewSection(PostEntity post) {
        System.out.println("🔄 Toggling review section for post: " + post.getPostId());
        System.out.println("🔍 Current visible: " + reviewSection.isVisible());
        System.out.println("🔍 Current post: " + (currentPostForReview != null ? currentPostForReview.getPostId() : "null"));
        
        // Hide comment section if showing reviews
        if (commentSection.isVisible()) {
            commentSection.setVisible(false);
            commentSection.setManaged(false);
            System.out.println("🔽 Hid comment section");
        }
        
        if (reviewSection.isVisible() && currentPostForReview != null && currentPostForReview.getPostId() == post.getPostId()) {
            // Hide if same post
            reviewSection.setVisible(false);
            reviewSection.setManaged(false);
            currentPostForReview = null;
            System.out.println("🔽 Hiding review section for same post: " + post.getPostId());
        } else {
            // Show review for this post
            currentPostForReview = post;
            reviewSection.setVisible(true);
            reviewSection.setManaged(true);
            reviewCommentField.requestFocus();
            System.out.println("🔼 Showing review section for post: " + post.getPostId());
        }
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
        System.out.println("📂 Starting to load comments for post: " + post.getPostId());
        
        // Get or create comments for this post
        List<CommentEntity> comments = postComments.computeIfAbsent(post.getPostId(), k -> createSampleComments(post.getPostId()));
        System.out.println("📝 Found " + comments.size() + " comments");
        
        // Clear existing comments display
        commentsContainer.getChildren().clear();
        System.out.println("🧹 Cleared existing comments display");
        
        // Display each comment
        for (int i = 0; i < comments.size(); i++) {
            CommentEntity comment = comments.get(i);
            System.out.println("📄 Creating comment card " + (i+1) + ": " + comment.getContent());
            VBox commentCard = createCommentCard(comment);
            commentsContainer.getChildren().add(commentCard);
        }
        
        System.out.println("✅ Added " + commentsContainer.getChildren().size() + " comment cards to container");
        
        showAlert("Info", "Loaded " + comments.size() + " comments for post " + post.getPostId());
        System.out.println("📂 Finished loading comments for post: " + post.getPostId());
    }
    
    private List<CommentEntity> createSampleComments(int postId) {
        List<CommentEntity> comments = new ArrayList<>();
        
        // Sample comments
        comments.add(new CommentEntity(1, "Great post! Really helpful content.", 
            LocalDateTime.now().minusHours(2), postId, 2));
        comments.add(new CommentEntity(2, "Thanks for sharing this! 🙏", 
            LocalDateTime.now().minusHours(1), postId, 3));
        comments.add(new CommentEntity(3, "Looking forward to more content like this.", 
            LocalDateTime.now().minusMinutes(30), postId, 1));
        
        return comments;
    }
    
    private VBox createCommentCard(CommentEntity comment) {
        System.out.println("🎨 Creating comment card for: " + comment.getContent());
        
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8px; -fx-padding: 12px; " +
                   "-fx-border-color: #e0e0e0; -fx-border-radius: 8px; -fx-border-width: 1px;");
        
        // Comment header
        HBox header = new HBox(8);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label authorLabel = new Label("User " + comment.getUserId());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1d9bf0;");
        
        Label timeLabel = new Label(formatTimeAgo(comment.getCreatedAt()));
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        
        header.getChildren().addAll(authorLabel, timeLabel);
        
        // Comment content
        Label contentLabel = new Label(comment.getContent());
        contentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333; -fx-wrap-text: true;");
        contentLabel.setPrefWidth(350);
        
        card.getChildren().addAll(header, contentLabel);
        
        System.out.println("✅ Comment card created with " + card.getChildren().size() + " children");
        return card;
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
        try {
            List<CommentEntity> comments = commentService.findByPostId(post.getPostId());
            return comments.size();
        } catch (Exception e) {
            System.err.println("❌ Error getting comment count for post " + post.getPostId() + ": " + e.getMessage());
            return 0;
        }
    }
    
    // ==================== ID GENERATORS ===================
    
    private int generatePostId() {
        return allPosts.size() + 1;
    }
    
    private int generateReviewId() {
        return (int) (Math.random() * 10000);
    }
    
    // ==================== INLINE COMMENTS ===================
    
    /**
     * Creates an inline comments section for a post
     */
    private VBox createInlineCommentsSection(PostEntity post) {
        VBox commentsSection = new VBox(10);
        commentsSection.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 15;");
        
        // Comments header
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label commentsLabel = new Label("Comments");
        commentsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #495057;");
        
        headerBox.getChildren().add(commentsLabel);
        
        // Comments container
        VBox commentsContainer = new VBox(8);
        commentsContainer.setPrefWidth(400);
        
        // Load existing comments from database
        try {
            List<CommentEntity> comments = commentService.findByPostId(post.getPostId());
            
            // Store in local cache
            postComments.put(post.getPostId(), comments);
            
            for (CommentEntity comment : comments) {
                commentsContainer.getChildren().add(createCommentItem(comment));
            }
            
            if (comments.isEmpty()) {
                Label noCommentsLabel = new Label("No comments yet. Be the first to comment!");
                noCommentsLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
                commentsContainer.getChildren().add(noCommentsLabel);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading comments for post " + post.getPostId() + ": " + e.getMessage());
            Label errorLabel = new Label("Error loading comments");
            errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-style: italic;");
            commentsContainer.getChildren().add(errorLabel);
        }
        
        // Add comment section
        HBox addCommentBox = new HBox(10);
        addCommentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        TextArea commentField = new TextArea();
        commentField.setPromptText("Write a comment...");
        commentField.setPrefHeight(60);
        commentField.setPrefWidth(300);
        commentField.setWrapText(true);
        commentField.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 6;");
        
        Button submitButton = new Button("Comment");
        submitButton.setStyle("-fx-background-color: #1d9bf0; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        submitButton.setOnAction(e -> addInlineComment(post, commentField, commentsContainer));
        
        addCommentBox.getChildren().addAll(commentField, submitButton);
        
        commentsSection.getChildren().addAll(headerBox, commentsContainer, addCommentBox);
        
        return commentsSection;
    }
    
    /**
     * Creates a comment item UI component
     */
    private HBox createCommentItem(CommentEntity comment) {
        HBox commentBox = new HBox(10);
        commentBox.setStyle("-fx-background-color: white; -fx-background-radius: 6; -fx-padding: 10;");
        
        // Avatar
        javafx.scene.shape.Circle avatar = new javafx.scene.shape.Circle(12);
        avatar.setStyle("-fx-fill: #007bff;");
        
        // Comment content
        VBox commentContent = new VBox(4);
        commentContent.setPrefWidth(350);
        
        // Author and time
        HBox headerBox = new HBox(8);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label authorLabel = new Label("User " + comment.getUserId());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #495057;");
        
        Label timeLabel = new Label(formatTimeAgo(comment.getCreatedAt()));
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
        
        headerBox.getChildren().addAll(authorLabel, timeLabel);
        
        // Comment text
        Label commentLabel = new Label(comment.getContent());
        commentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #212529; -fx-wrap-text: true;");
        
        commentContent.getChildren().addAll(headerBox, commentLabel);
        commentBox.getChildren().addAll(avatar, commentContent);
        
        return commentBox;
    }
    
    /**
     * Toggles inline comments visibility for a post
     */
    private void toggleInlineComments(PostEntity post, VBox card) {
        VBox commentsSection = commentUIComponents.get(post.getPostId());
        if (commentsSection != null) {
            boolean isVisible = commentsSection.isVisible();
            commentsSection.setVisible(!isVisible);
            commentsSection.setManaged(!isVisible);
            
            System.out.println("🔄 " + (isVisible ? "Hiding" : "Showing") + " inline comments for post: " + post.getPostId());
        }
    }
    
    /**
     * Adds an inline comment to a post
     */
    private void addInlineComment(PostEntity post, TextArea commentField, VBox commentsContainer) {
        String content = commentField.getText().trim();
        
        if (content.isEmpty()) {
            showAlert("Empty Comment", "Please write a comment before submitting.");
            return;
        }
        
        try {
            // Debug logging
            System.out.println("🔍 Creating comment with:");
            System.out.println("   Post ID: " + post.getPostId());
            System.out.println("   User ID: " + currentUserId);
            System.out.println("   Content: '" + content + "'");
            System.out.println("   Created at: " + LocalDateTime.now());
            
            // Create new comment entity
            CommentEntity comment = new CommentEntity();
            comment.setPostId(post.getPostId());
            comment.setUserId(currentUserId);
            comment.setContent(content);
            comment.setCreatedAt(LocalDateTime.now());
            
            // Save comment to database using service layer
            CommentEntity createdComment = commentService.create(comment);
            
            // Add to local data structure
            List<CommentEntity> comments = postComments.computeIfAbsent(post.getPostId(), k -> new ArrayList<>());
            comments.add(createdComment);
            
            // Remove "no comments" label if present
            if (!commentsContainer.getChildren().isEmpty() && 
                commentsContainer.getChildren().get(0) instanceof Label && 
                ((Label) commentsContainer.getChildren().get(0)).getText().contains("No comments yet")) {
                commentsContainer.getChildren().clear();
            }
            
            // Add to UI
            commentsContainer.getChildren().add(createCommentItem(createdComment));
            
            // Clear comment field
            commentField.clear();
            
            System.out.println("✅ Comment added to database with ID: " + createdComment.getCommentId());
            
        } catch (Exception e) {
            System.err.println("❌ Error adding comment: " + (e.getMessage() != null ? e.getMessage() : "null message"));
            System.err.println("❌ Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            showAlert("Database Error", "Failed to add comment. Please try again.");
        }
    }
    
    /**
     * Generates a unique comment ID
     */
    private int generateCommentId() {
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
