package org.example.bloggingapp.Models;

import java.time.LocalDateTime;

public class PostHashtagEntity {

    private int postHashtagId;
    private int postId;
    private int hashtagId;
    private LocalDateTime createdAt;

    public PostHashtagEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public PostHashtagEntity(int postHashtagId, int postId, int hashtagId) {
        this.postHashtagId = postHashtagId;
        this.postId = postId;
        this.hashtagId = hashtagId;
        this.createdAt = LocalDateTime.now();
    }

    public PostHashtagEntity(int postId, int hashtagId) {
        this.postId = postId;
        this.hashtagId = hashtagId;
        this.createdAt = LocalDateTime.now();
    }

    public int getPostHashtagId() {
        return postHashtagId;
    }

    public void setPostHashtagId(int postHashtagId) {
        this.postHashtagId = postHashtagId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(int hashtagId) {
        this.hashtagId = hashtagId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
