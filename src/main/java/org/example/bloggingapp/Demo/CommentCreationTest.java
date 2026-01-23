package org.example.bloggingapp.Demo;

import org.example.bloggingapp.Database.Repositories.CommentRepository;
import org.example.bloggingapp.Services.CommentService;
import org.example.bloggingapp.Models.CommentEntity;

import java.time.LocalDateTime;

public class CommentCreationTest {
    
    public static void main(String[] args) {
        System.out.println("Testing comment creation...");
        
        try {
            // Test repository directly
            System.out.println("\n=== Testing CommentRepository directly ===");
            CommentRepository commentRepository = new CommentRepository();
            
            CommentEntity comment = new CommentEntity();
            comment.setContent("Test comment from repository");
            comment.setPostId(1);
            comment.setUserId(1);
            comment.setCreatedAt(LocalDateTime.now());
            
            System.out.println("Creating comment with content: " + comment.getContent());
            System.out.println("Post ID: " + comment.getPostId());
            System.out.println("User ID: " + comment.getUserId());
            
            commentRepository.create(comment);
            System.out.println("✅ Comment created with ID: " + comment.getCommentId());
            
            // Test service layer
            System.out.println("\n=== Testing CommentService ===");
            CommentService commentService = new CommentService(commentRepository);
            
            CommentEntity comment2 = new CommentEntity();
            comment2.setContent("Test comment from service");
            comment2.setPostId(1);
            comment2.setUserId(1);
            comment2.setCreatedAt(LocalDateTime.now());
            
            CommentEntity createdComment = commentService.create(comment2);
            System.out.println("✅ Comment created via service with ID: " + createdComment.getCommentId());
            
        } catch (Exception e) {
            System.err.println("❌ Error during comment creation test: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Test completed ===");
    }
}
