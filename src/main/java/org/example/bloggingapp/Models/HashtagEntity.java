package org.example.bloggingapp.Models;

import java.time.LocalDateTime;

public class HashtagEntity {

    private int hashtagId;
    private String tag;
    private LocalDateTime createdAt;
    private int usageCount;

    public HashtagEntity() {
        this.usageCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public HashtagEntity(int hashtagId, String tag) {
        this.hashtagId = hashtagId;
        this.tag = tag.toLowerCase(); // Store hashtags in lowercase for consistency
        this.usageCount = 1;
        this.createdAt = LocalDateTime.now();
    }

    public HashtagEntity(int hashtagId, String tag, int usageCount, LocalDateTime createdAt) {
        this.hashtagId = hashtagId;
        this.tag = tag.toLowerCase();
        this.usageCount = usageCount;
        this.createdAt = createdAt;
    }

    public int getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(int hashtagId) {
        this.hashtagId = hashtagId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag.toLowerCase();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    @Override
    public String toString() {
        return "#" + tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HashtagEntity that = (HashtagEntity) obj;
        return tag != null ? tag.equalsIgnoreCase(that.tag) : that.tag == null;
    }

    @Override
    public int hashCode() {
        return tag != null ? tag.toLowerCase().hashCode() : 0;
    }
}
