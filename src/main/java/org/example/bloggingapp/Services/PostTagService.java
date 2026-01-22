package org.example.bloggingapp.Services;

import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.Repositories.PostTagRepository;
import org.example.bloggingapp.Models.PostTagEntity;

import java.util.List;

public class PostTagService implements IService<PostTagEntity> {
    
    private final PostTagRepository postTagRepository;
    
    public PostTagService(PostTagRepository postTagRepository) {
        this.postTagRepository = postTagRepository;
    }
    
    @Override
    public PostTagEntity create(PostTagEntity postTag) {
        postTagRepository.create(postTag);
        return postTag;
    }
    
    @Override
    public PostTagEntity findById(int id) {
        return postTagRepository.findByInteger(id);
    }
    
    @Override
    public PostTagEntity findByString(String identifier) {
        return postTagRepository.findByString(identifier);
    }
    
    @Override
    public List<PostTagEntity> findAll() {
        return postTagRepository.findAll();
    }
    
    @Override
    public PostTagEntity update(int id, PostTagEntity postTag) {
        PostTagEntity existingPostTag = findById(id);
        if (existingPostTag != null) {
            postTagRepository.updateById(id);
            return postTag;
        }
        return null;
    }
    
    @Override
    public boolean delete(int id) {
        PostTagEntity existingPostTag = findById(id);
        if (existingPostTag != null) {
            postTagRepository.delete(id);
            return true;
        }
        return false;
    }
    
    public List<PostTagEntity> findByPostId(int postId) {
        return findAll().stream()
                .filter(postTag -> postTag.getPostId() == postId)
                .toList();
    }
    
    public List<PostTagEntity> findByTagId(int tagId) {
        return findAll().stream()
                .filter(postTag -> postTag.getTagId() == tagId)
                .toList();
    }
    
    public boolean addTagToPost(int postId, int tagId) {
        PostTagEntity postTag = new PostTagEntity(postId, tagId);
        create(postTag);
        return true;
    }
    
    public boolean removeTagFromPost(int postId, int tagId) {
        List<PostTagEntity> existingRelations = findAll().stream()
                .filter(pt -> pt.getPostId() == postId && pt.getTagId() == tagId)
                .toList();
        
        if (!existingRelations.isEmpty()) {
            postTagRepository.delete(existingRelations.get(0).getPostId());
            return true;
        }
        return false;
    }
}
