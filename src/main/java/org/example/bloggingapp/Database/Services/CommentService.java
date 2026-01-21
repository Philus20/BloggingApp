package org.example.bloggingapp.Database.Services;

import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.Repositories.CommentRepository;
import org.example.bloggingapp.Models.CommentEntity;

import java.time.LocalDateTime;
import java.util.List;

public class CommentService implements IService<CommentEntity> {
    
    private final CommentRepository commentRepository;
    
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    
    @Override
    public CommentEntity create(CommentEntity comment) {
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }
        try {
            commentRepository.create(comment);
            return comment;
        } catch (Exception e) {
            System.err.println("❌ CommentService.create() failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create comment: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CommentEntity findById(int id) {
        return commentRepository.findByInteger(id);
    }
    
    @Override
    public CommentEntity findByString(String identifier) {
        return commentRepository.findByString(identifier);
    }
    
    @Override
    public List<CommentEntity> findAll() {
        return commentRepository.findAll();
    }
    
    @Override
    public CommentEntity update(int id, CommentEntity comment) {
        CommentEntity existingComment = findById(id);
        if (existingComment != null) {
            comment.setCommentId(id);
            commentRepository.updateById(id);
            return comment;
        }
        return null;
    }
    
    @Override
    public boolean delete(int id) {
        CommentEntity existingComment = findById(id);
        if (existingComment != null) {
            commentRepository.delete(id);
            return true;
        }
        return false;
    }
    
    public List<CommentEntity> findByPostId(int postId) {
        return findAll().stream()
                .filter(comment -> comment.getPostId() == postId)
                .toList();
    }
    
    public List<CommentEntity> findByUserId(int userId) {
        return findAll().stream()
                .filter(comment -> comment.getUserId() == userId)
                .toList();
    }
}
