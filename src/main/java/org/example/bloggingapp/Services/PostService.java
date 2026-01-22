package org.example.bloggingapp.Services;

import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.Repositories.PostRepository;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.EntityNotFoundException;
import org.example.bloggingapp.Exceptions.ServiceException;
import org.example.bloggingapp.Exceptions.ValidationException;
import org.example.bloggingapp.Database.DbInterfaces.CacheService;
import org.example.bloggingapp.Cache.InMemoryCacheService;
import org.example.bloggingapp.Cache.CacheManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PostService implements IService<PostEntity> {
    
    private final PostRepository postRepository;
    private PostSearchService searchService;
    
    // Cache instances for performance optimization
    private final CacheService<Integer, PostEntity> postCache;
    private final CacheService<String, PostEntity> postByTitleCache;
    private final CacheService<Integer, List<PostEntity>> userPostsCache;
    private final CacheService<String, List<PostEntity>> allPostsCache;
    
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
        // Initialize caches with different configurations for different use cases
        // These caches will store real database values in memory for fast access
        this.postCache = new InMemoryCacheService<>(500, 10 * 60 * 1000); // 500 posts, 10 minutes
        this.postByTitleCache = new InMemoryCacheService<>(200, 15 * 60 * 1000); // 200 titles, 15 minutes
        this.userPostsCache = new InMemoryCacheService<>(100, 5 * 60 * 1000); // 100 users, 5 minutes
        this.allPostsCache = new InMemoryCacheService<>(10, 2 * 60 * 1000); // 10 lists, 2 minutes
        
        // Register caches with CacheManager for centralized management
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.registerCache("posts", (InMemoryCacheService<?, ?>) postCache);
        cacheManager.registerCache("postTitles", (InMemoryCacheService<?, ?>) postByTitleCache);
        cacheManager.registerCache("userPosts", (InMemoryCacheService<?, ?>) userPostsCache);
        cacheManager.registerCache("allPosts", (InMemoryCacheService<?, ?>) allPostsCache);
        
        // Pre-populate cache with real database values
        prepopulateCacheFromDatabase();
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
            
            // Cache the newly created post
            postCache.put(post.getPostId(), post);
            postByTitleCache.put(post.getTitle(), post);
            
            // Invalidate related caches
            invalidateRelatedCaches(post.getUserId());
            
            // Invalidate search service cache
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
            
            // Try cache first
            Optional<PostEntity> cachedPost = postCache.get(id);
            if (cachedPost.isPresent()) {
                return cachedPost.get();
            }
            
            // Cache miss - fetch from database
            PostEntity post = postRepository.findByInteger(id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }
            
            // Cache the result
            postCache.put(id, post);
            
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
            
            // Try cache first
            Optional<PostEntity> cachedPost = postByTitleCache.get(identifier);
            if (cachedPost.isPresent()) {
                return cachedPost.get();
            }
            
            // Cache miss - fetch from database
            PostEntity post = postRepository.findByString(identifier);
            
            if (post != null) {
                // Cache the result
                postByTitleCache.put(identifier, post);
                // Also cache by ID for faster access
                postCache.put(post.getPostId(), post);
            }
            
            return post;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_FIND_STRING_ERROR", "Failed to find post by identifier: " + identifier, e);
        }
    }
    
    @Override
    public List<PostEntity> findAll() throws DatabaseException {
        try {
            // Try cache first
            Optional<List<PostEntity>> cachedPosts = allPostsCache.get("all");
            if (cachedPosts.isPresent()) {
                return cachedPosts.get();
            }
            
            // Cache miss - fetch from database
            List<PostEntity> posts = postRepository.findAll();
            
            // Cache the result
            allPostsCache.put("all", posts);
            
            return posts;
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
                postRepository.updatePost(id, post);
                
                // Update cache
                postCache.put(id, post);
                postByTitleCache.put(post.getTitle(), post);
                
                // Invalidate related caches
                invalidateRelatedCaches(post.getUserId());
                
                // Invalidate search service cache
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
            
            // Get post before deletion to invalidate caches
            PostEntity postToDelete = findById(id);
            if (postToDelete != null) {
                postRepository.delete(id);
                
                // Remove from caches
                postCache.remove(id);
                postByTitleCache.remove(postToDelete.getTitle());
                
                // Invalidate related caches
                invalidateRelatedCaches(postToDelete.getUserId());
                
                // Invalidate search service cache
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
            
            // Try cache first
            Optional<List<PostEntity>> cachedPosts = userPostsCache.get(userId);
            if (cachedPosts.isPresent()) {
                return cachedPosts.get();
            }
            
            // Cache miss - fetch from database
            List<PostEntity> posts = findAll().stream()
                    .filter(post -> post.getUserId() == userId)
                    .toList();
            
            // Cache the result
            userPostsCache.put(userId, posts);
            
            return posts;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_FIND_USER_ERROR", "Failed to find posts by user ID: " + userId, e);
        }
    }
    
    public PostEntity findByTitle(String title) throws DatabaseException, ValidationException {
        return findByString(title); // Reuse the cached findByString method
    }
    
    /**
     * Invalidates caches that might be affected by changes to posts
     * @param userId the user ID whose posts cache should be invalidated
     */
    private void invalidateRelatedCaches(int userId) {
        allPostsCache.remove("all");
        userPostsCache.remove(userId);
    }
    
    /**
     * Pre-populates cache with real database values for better initial performance
     */
    private void prepopulateCacheFromDatabase() {
        try {
            // Load all posts from database and cache them
            List<PostEntity> allPosts = postRepository.findAll();
            if (!allPosts.isEmpty()) {
                allPostsCache.put("all", allPosts);
                
                // Cache individual posts and create user post mappings
                for (PostEntity post : allPosts) {
                    postCache.put(post.getPostId(), post);
                    postByTitleCache.put(post.getTitle(), post);
                    
                    // Add to user posts cache
                    List<PostEntity> existingUserPosts = userPostsCache.get(post.getUserId()).orElse(null);
                    List<PostEntity> userPosts = existingUserPosts != null ? existingUserPosts : new java.util.ArrayList<>();
                    userPosts.add(post);
                    userPostsCache.put(post.getUserId(), userPosts);
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to pre-populate cache from database: " + e.getMessage());
            // Continue without pre-population - cache will be populated on-demand
        }
    }
    
    /**
     * Clears all caches
     */
    public void clearAllCaches() {
        postCache.clear();
        postByTitleCache.clear();
        userPostsCache.clear();
        allPostsCache.clear();
    }
    
    /**
     * Returns cache statistics for monitoring
     */
    public String getCacheStats() {
        return String.format(
            "Post Cache Stats:\n" +
            "  Individual Posts: %s\n" +
            "  Posts by Title: %s\n" +
            "  User Posts: %s\n" +
            "  All Posts: %s",
            postCache.getStats(),
            postByTitleCache.getStats(),
            userPostsCache.getStats(),
            allPostsCache.getStats()
        );
    }
    
    /**
     * Performs cleanup of expired entries
     */
    public void cleanupCaches() {
        if (postCache instanceof InMemoryCacheService) {
            ((InMemoryCacheService<Integer, PostEntity>) postCache).cleanupExpired();
        }
        if (postByTitleCache instanceof InMemoryCacheService) {
            ((InMemoryCacheService<String, PostEntity>) postByTitleCache).cleanupExpired();
        }
        if (userPostsCache instanceof InMemoryCacheService) {
            ((InMemoryCacheService<Integer, List<PostEntity>>) userPostsCache).cleanupExpired();
        }
        if (allPostsCache instanceof InMemoryCacheService) {
            ((InMemoryCacheService<String, List<PostEntity>>) allPostsCache).cleanupExpired();
        }
    }
}
