package org.example.bloggingapp.Database.factories;

import org.example.bloggingapp.Database.Repositories.CommentRepository;
import org.example.bloggingapp.Database.Repositories.PostRepository;
import org.example.bloggingapp.Database.Repositories.ReviewRepository;
import org.example.bloggingapp.Database.Repositories.UserRepository;
import org.example.bloggingapp.Database.Services.CommentService;
import org.example.bloggingapp.Database.Services.PostService;
import org.example.bloggingapp.Database.Services.ReviewService;
import org.example.bloggingapp.Database.Services.UserService;

/**
 * 🏭 Service Factory - Manages service instances following clean architecture
 * 
 * This factory creates and manages all service instances, ensuring that
 * controllers only interact with services, not repositories directly.
 */
public class ServiceFactory {
    
    private static ServiceFactory instance;
    
    // Service instances
    private PostService postService;
    private CommentService commentService;
    private UserService userService;
    private ReviewService reviewService;
    
    private ServiceFactory() {
        initializeServices();
    }
    
    /**
     * Gets the singleton instance of ServiceFactory
     */
    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }
    
    /**
     * Initializes all service instances with their respective repositories
     */
    private void initializeServices() {
        try {
            // Initialize repositories
            PostRepository postRepository = new PostRepository();
            CommentRepository commentRepository = new CommentRepository();
            UserRepository userRepository = new UserRepository();
            ReviewRepository reviewRepository = new ReviewRepository();
            
            // Initialize services with repositories
            this.postService = new PostService(postRepository);
            this.commentService = new CommentService(commentRepository);
            this.userService = new UserService(userRepository);
            this.reviewService = new ReviewService(reviewRepository);
            
            System.out.println("✅ All services initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize services: " + e.getMessage());
            throw new RuntimeException("Service initialization failed", e);
        }
    }
    
    // ==================== SERVICE GETTERS ====================
    
    public PostService getPostService() {
        return postService;
    }
    
    public CommentService getCommentService() {
        return commentService;
    }
    
    public UserService getUserService() {
        return userService;
    }
    
    public ReviewService getReviewService() {
        return reviewService;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Refreshes all service instances (useful for testing or reconnection)
     */
    public void refreshServices() {
        initializeServices();
    }
    
    /**
     * Checks if all services are properly initialized
     */
    public boolean isHealthy() {
        return postService != null && 
               commentService != null && 
               userService != null && 
               reviewService != null;
    }
}
