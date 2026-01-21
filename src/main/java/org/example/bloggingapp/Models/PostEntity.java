package org.example.bloggingapp.Models;

import java.time.LocalDateTime;

public class PostEntity {

    private int postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int userId;
    private String status;
    private int views;
    private String authorName;

    public PostEntity() {
        // Initialize default values
        this.status = "Draft";
        this.views = 0;
        this.authorName = "";
    }

    public PostEntity(int postId, String title, String content, LocalDateTime createdAt, int userId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.status = "Draft";
        this.views = 0;
        this.authorName = "";
    }

    public PostEntity(int postId, String title, String content, LocalDateTime createdAt, int userId, String status, int views, String authorName) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.status = status;
        this.views = views;
        this.authorName = authorName;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
