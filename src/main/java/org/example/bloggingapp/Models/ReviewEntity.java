package org.example.bloggingapp.Models;

import java.time.LocalDateTime;

public class ReviewEntity {

    private int reviewId;
    private int rating;
    private String comment;
    private int userId;
    private int postId;
    private LocalDateTime createdAt;

    public ReviewEntity() {
    }

    public ReviewEntity(int reviewId, int rating, String comment, int userId, int postId) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comment = comment;
        this.userId = userId;
        this.postId = postId;
        this.createdAt = LocalDateTime.now();
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
