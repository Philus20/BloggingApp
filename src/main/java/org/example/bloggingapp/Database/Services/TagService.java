package org.example.bloggingapp.Database.Services;

import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.Repositories.TagRepository;
import org.example.bloggingapp.Models.TagEntity;

import java.util.List;

public class TagService implements IService<TagEntity> {
    
    private final TagRepository tagRepository;
    
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    
    @Override
    public TagEntity create(TagEntity tag) {
        tagRepository.create(tag);
        return tag;
    }
    
    @Override
    public TagEntity findById(int id) {
        return tagRepository.findByInteger(id);
    }
    
    @Override
    public TagEntity findByString(String identifier) {
        return tagRepository.findByString(identifier);
    }
    
    @Override
    public List<TagEntity> findAll() {
        return tagRepository.findAll();
    }
    
    @Override
    public TagEntity update(int id, TagEntity tag) {
        TagEntity existingTag = findById(id);
        if (existingTag != null) {
            tag.setTagId(id);
            tagRepository.updateById(id);
            return tag;
        }
        return null;
    }
    
    @Override
    public boolean delete(int id) {
        TagEntity existingTag = findById(id);
        if (existingTag != null) {
            tagRepository.delete(id);
            return true;
        }
        return false;
    }
    
    public TagEntity findByName(String name) {
        return tagRepository.findByString(name);
    }
}
