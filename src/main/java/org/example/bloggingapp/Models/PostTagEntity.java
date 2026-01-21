package org.example.bloggingapp.Models;

public class PostTagEntity {

    private int postId;
    private int tagId;

    public PostTagEntity() {
    }

    public PostTagEntity(int postId, int tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
