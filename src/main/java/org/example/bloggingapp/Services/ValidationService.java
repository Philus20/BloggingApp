package org.example.bloggingapp.Services;

import org.example.bloggingapp.Database.DbInterfaces.IValidationService;
import org.example.bloggingapp.Utils.RegexPatterns;

import java.util.HashMap;
import java.util.Map;

public class ValidationService implements IValidationService {
    
    @Override
    public boolean isValidEmail(String email) {
        return RegexPatterns.matches(email, RegexPatterns.EMAIL);
    }
    
    @Override
    public boolean isValidUsername(String username) {
        return RegexPatterns.matches(username, RegexPatterns.USERNAME);
    }
    
    @Override
    public boolean isValidPassword(String password) {
        return RegexPatterns.matches(password, RegexPatterns.STRONG_PASSWORD);
    }
    
    @Override
    public boolean isValidPostTitle(String title) {
        return RegexPatterns.isLengthValid(title, 1, 100);
    }
    
    @Override
    public boolean isValidPostContent(String content) {
        return RegexPatterns.isLengthValid(content, 1, 5000);
    }
    
    @Override
    public boolean isValidCommentContent(String content) {
        return RegexPatterns.isLengthValid(content, 1, 1000);
    }
    
    @Override
    public boolean isValidTagName(String tagName) {
        return RegexPatterns.matches(tagName, RegexPatterns.TAG_NAME);
    }
    
    @Override
    public boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
    
    @Override
    public boolean isValidReviewComment(String comment) {
        if (RegexPatterns.isNullOrEmpty(comment)) {
            return true; // Review comment is optional
        }
        return comment.trim().length() <= 500;
    }
    
    @Override
    public Map<String, String> validateUser(String username, String email, String password, String role) {
        Map<String, String> errors = new HashMap<>();
        
        if (!isValidUsername(username)) {
            errors.put("username", "Username must be 3-20 characters long and contain only letters, numbers, and underscores");
        }
        
        if (!isValidEmail(email)) {
            errors.put("email", "Please enter a valid email address");
        }
        
        if (!isValidPassword(password)) {
            errors.put("password", "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
        }
        
        if (role == null || role.trim().isEmpty()) {
            errors.put("role", "Role cannot be empty");
        }
        
        return errors;
    }
    
    @Override
    public Map<String, String> validatePost(String title, String content, Integer userId) {
        Map<String, String> errors = new HashMap<>();
        
        if (!isValidPostTitle(title)) {
            errors.put("title", "Title must be 1-100 characters long and cannot be empty");
        }
        
        if (!isValidPostContent(content)) {
            errors.put("content", "Content must be 1-5000 characters long and cannot be empty");
        }
        
        if (userId == null || userId <= 0) {
            errors.put("userId", "Valid user ID is required");
        }
        
        return errors;
    }
    
    @Override
    public Map<String, String> validateComment(String content, Integer postId, Integer userId) {
        Map<String, String> errors = new HashMap<>();
        
        if (!isValidCommentContent(content)) {
            errors.put("content", "Comment must be 1-1000 characters long and cannot be empty");
        }
        
        if (postId == null || postId <= 0) {
            errors.put("postId", "Valid post ID is required");
        }
        
        if (userId == null || userId <= 0) {
            errors.put("userId", "Valid user ID is required");
        }
        
        return errors;
    }
    
    @Override
    public Map<String, String> validateReview(Integer rating, String comment, Integer userId, Integer postId) {
        Map<String, String> errors = new HashMap<>();
        
        if (!isValidRating(rating)) {
            errors.put("rating", "Rating must be between 1 and 5");
        }
        
        if (!isValidReviewComment(comment)) {
            errors.put("comment", "Review comment must be 500 characters or less");
        }
        
        if (userId == null || userId <= 0) {
            errors.put("userId", "Valid user ID is required");
        }
        
        if (postId == null || postId <= 0) {
            errors.put("postId", "Valid post ID is required");
        }
        
        return errors;
    }
}
