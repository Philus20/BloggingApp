package org.example.bloggingapp.Database.DbInterfaces;

import java.util.Map;

public interface IValidationService {
    
    /**
     * Validates email format
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    boolean isValidEmail(String email);
    
    /**
     * Validates username format (alphanumeric, underscores, 3-20 characters)
     * @param username the username to validate
     * @return true if valid, false otherwise
     */
    boolean isValidUsername(String username);
    
    /**
     * Validates password strength (min 8 chars, at least 1 uppercase, 1 lowercase, 1 digit, 1 special char)
     * @param password the password to validate
     * @return true if valid, false otherwise
     */
    boolean isValidPassword(String password);
    
    /**
     * Validates post title (non-empty, 1-100 characters)
     * @param title the title to validate
     * @return true if valid, false otherwise
     */
    boolean isValidPostTitle(String title);
    
    /**
     * Validates post content (non-empty, 1-5000 characters)
     * @param content the content to validate
     * @return true if valid, false otherwise
     */
    boolean isValidPostContent(String content);
    
    /**
     * Validates comment content (non-empty, 1-1000 characters)
     * @param content the content to validate
     * @return true if valid, false otherwise
     */
    boolean isValidCommentContent(String content);
    
    /**
     * Validates tag name (alphanumeric, spaces, hyphens, 1-50 characters)
     * @param tagName the tag name to validate
     * @return true if valid, false otherwise
     */
    boolean isValidTagName(String tagName);
    
    /**
     * Validates rating (1-5)
     * @param rating the rating to validate
     * @return true if valid, false otherwise
     */
    boolean isValidRating(int rating);
    
    /**
     * Validates review comment (optional, max 500 characters)
     * @param comment the comment to validate
     * @return true if valid, false otherwise
     */
    boolean isValidReviewComment(String comment);
    
    /**
     * Validates all fields for a user entity
     * @param username the username
     * @param email the email
     * @param password the password
     * @param role the role
     * @return map of field names to error messages (empty if all valid)
     */
    Map<String, String> validateUser(String username, String email, String password, String role);
    
    /**
     * Validates all fields for a post entity
     * @param title the title
     * @param content the content
     * @param userId the user ID
     * @return map of field names to error messages (empty if all valid)
     */
    Map<String, String> validatePost(String title, String content, Integer userId);
    
    /**
     * Validates all fields for a comment entity
     * @param content the content
     * @param postId the post ID
     * @param userId the user ID
     * @return map of field names to error messages (empty if all valid)
     */
    Map<String, String> validateComment(String content, Integer postId, Integer userId);
    
    /**
     * Validates all fields for a review entity
     * @param rating the rating
     * @param comment the comment
     * @param userId the user ID
     * @param postId the post ID
     * @return map of field names to error messages (empty if all valid)
     */
    Map<String, String> validateReview(Integer rating, String comment, Integer userId, Integer postId);
}
