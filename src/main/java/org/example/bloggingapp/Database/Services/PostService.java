package org.example.bloggingapp.Database.Services;

import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.Repositories.PostRepository;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.EntityNotFoundException;
import org.example.bloggingapp.Exceptions.ServiceException;
import org.example.bloggingapp.Exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class PostService implements IService<PostEntity> {
    
    private final PostRepository postRepository;
    private PostSearchService searchService;
    
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public void setSearchService(PostSearchService searchService) {
        this.searchService = searchService;
    }
    
    @Override
    public PostEntity create(PostEntity post) throws DatabaseException, ServiceException, ValidationException {
        try {
            if (post == null) {
                throw new ValidationException("POST_NULL", "post", "Post entity cannot be null");
            }
            if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
                throw new ValidationException("TITLE_REQUIRED", "title", "Post title is required");
            }
            if (post.getContent() == null || post.getContent().trim().isEmpty()) {
                throw new ValidationException("CONTENT_REQUIRED", "content", "Post content is required");
            }
            
            if (post.getCreatedAt() == null) {
                post.setCreatedAt(LocalDateTime.now());
            }
            
            postRepository.create(post);
            
            // Invalidate cache when new post is created
            if (searchService != null) {
                searchService.invalidateCache();
            }
            
            return post;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_CREATE_ERROR", "Failed to create post", e);
        }
    }
    
    @Override
    public PostEntity findById(int id) throws DatabaseException, EntityNotFoundException, ValidationException {
        try {
            if (id <= 0) {
                throw new ValidationException("INVALID_ID", "id", "Post ID must be positive");
            }
            
            PostEntity post = postRepository.findByInteger(id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }
            return post;
        } catch (ValidationException | EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_FIND_ERROR", "Failed to find post by ID: " + id, e);
        }
    }
    
    @Override
    public PostEntity findByString(String identifier) throws DatabaseException, ValidationException {
        try {
            if (identifier == null || identifier.trim().isEmpty()) {
                throw new ValidationException("IDENTIFIER_REQUIRED", "identifier", "Identifier cannot be null or empty");
            }
            return postRepository.findByString(identifier);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_FIND_STRING_ERROR", "Failed to find post by identifier: " + identifier, e);
        }
    }
    
    @Override
    public List<PostEntity> findAll() throws DatabaseException {
        try {
            return postRepository.findAll();
        } catch (Exception e) {
            throw new DatabaseException("POST_FIND_ALL_ERROR", "Failed to find all posts", e);
        }
    }
    
    @Override
    public PostEntity update(int id, PostEntity post) throws DatabaseException, EntityNotFoundException, ValidationException {
        try {
            if (post == null) {
                throw new ValidationException("POST_NULL", "post", "Post entity cannot be null");
            }
            
            PostEntity existingPost = findById(id);
            if (existingPost != null) {
                post.setPostId(id);
                postRepository.updateById(id);
                
                // Invalidate cache when post is updated
                if (searchService != null) {
                    searchService.invalidateCache();
                }
                
                return post;
            }
            return null;
        } catch (ValidationException | EntityNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_UPDATE_ERROR", "Failed to update post: " + id, e);
        }
    }
    
    @Override
    public boolean delete(int id) throws DatabaseException, EntityNotFoundException, ValidationException {
        try {
            if (id <= 0) {
                throw new ValidationException("INVALID_ID", "id", "Post ID must be positive");
            }
            
            PostEntity existingPost = findById(id);
            if (existingPost != null) {
                postRepository.delete(id);
                
                // Invalidate cache when post is deleted
                if (searchService != null) {
                    searchService.invalidateCache();
                }
                
                return true;
            }
            return false;
        } catch (ValidationException | EntityNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_DELETE_ERROR", "Failed to delete post: " + id, e);
        }
    }
    
    public List<PostEntity> findByUserId(int userId) throws DatabaseException, ValidationException {
        try {
            if (userId <= 0) {
                throw new ValidationException("INVALID_USER_ID", "userId", "User ID must be positive");
            }
            
            return findAll().stream()
                    .filter(post -> post.getUserId() == userId)
                    .toList();
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_FIND_USER_ERROR", "Failed to find posts by user ID: " + userId, e);
        }
    }
    
    public PostEntity findByTitle(String title) throws DatabaseException, ValidationException {
        try {
            if (title == null || title.trim().isEmpty()) {
                throw new ValidationException("TITLE_REQUIRED", "title", "Title cannot be null or empty");
            }
            
            return postRepository.findByString(title);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_FIND_TITLE_ERROR", "Failed to find post by title: " + title, e);
        }
    }
}
