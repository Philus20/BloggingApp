package org.example.bloggingapp.Models;

import java.time.LocalDateTime;

public class CommentEntity {

    private int commentId;
    private String content;
    private LocalDateTime createdAt;
    private int postId;
    private int userId;

    public CommentEntity() {
    }

    public CommentEntity(int commentId, String content, LocalDateTime createdAt, int postId, int userId) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.postId = postId;
        this.userId = userId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
