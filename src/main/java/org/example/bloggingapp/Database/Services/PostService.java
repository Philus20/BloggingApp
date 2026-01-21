package org.example.bloggingapp.Database.Services;

import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.Repositories.PostRepository;
import org.example.bloggingapp.Models.PostEntity;

import java.time.LocalDateTime;
import java.util.List;

public class PostService implements IService<PostEntity> {
    
    private final PostRepository postRepository;
    
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    @Override
    public PostEntity create(PostEntity post) {
        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        postRepository.create(post);
        return post;
    }
    
    @Override
    public PostEntity findById(int id) {
        return postRepository.findByInteger(id);
    }
    
    @Override
    public PostEntity findByString(String identifier) {
        return postRepository.findByString(identifier);
    }
    
    @Override
    public List<PostEntity> findAll() {
        return postRepository.findAll();
    }
    
    @Override
    public PostEntity update(int id, PostEntity post) {
        PostEntity existingPost = findById(id);
        if (existingPost != null) {
            post.setPostId(id);
            postRepository.updateById(id);
            return post;
        }
        return null;
    }
    
    @Override
    public boolean delete(int id) {
        PostEntity existingPost = findById(id);
        if (existingPost != null) {
            postRepository.delete(id);
            return true;
        }
        return false;
    }
    
    public List<PostEntity> findByUserId(int userId) {
        return findAll().stream()
                .filter(post -> post.getUserId() == userId)
                .toList();
    }
    
    public PostEntity findByTitle(String title) {
        return postRepository.findByString(title);
    }
}
