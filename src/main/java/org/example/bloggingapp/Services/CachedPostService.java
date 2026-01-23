package org.example.bloggingapp.Services;

import org.example.bloggingapp.Database.DbInterfaces.CacheService;
import org.example.bloggingapp.Cache.InMemoryCacheService;
import org.example.bloggingapp.Cache.CacheManager;
import org.example.bloggingapp.Database.Repositories.PostRepository;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Utils.Exceptions.DatabaseException;
import org.example.bloggingapp.Utils.Exceptions.EntityNotFoundException;
import org.example.bloggingapp.Utils.Exceptions.ServiceException;
import org.example.bloggingapp.Utils.Exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

/**
 * Cached version of PostService that provides fast loading through in-memory caching
 */
public class CachedPostService extends PostService {
    
    private final CacheService<Integer, PostEntity> postCache;
    private final CacheService<String, PostEntity> postByTitleCache;
    private final CacheService<Integer, List<PostEntity>> userPostsCache;
    private final CacheService<String, List<PostEntity>> allPostsCache;
    
    public CachedPostService(PostRepository postRepository) {
        super(postRepository);
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
            PostEntity post = super.findById(id);
            
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
            PostEntity post = super.findByString(identifier);
            
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
            List<PostEntity> posts = super.findAll();
            
            // Cache the result
            allPostsCache.put("all", posts);
            
            return posts;
        } catch (Exception e) {
            throw new DatabaseException("POST_FIND_ALL_ERROR", "Failed to find all posts", e);
        }
    }
    
    @Override
    public PostEntity create(PostEntity post) throws DatabaseException, ServiceException, ValidationException {
        try {
            PostEntity createdPost = super.create(post);
            
            // Cache the newly created post
            postCache.put(createdPost.getPostId(), createdPost);
            postByTitleCache.put(createdPost.getTitle(), createdPost);
            
            // Invalidate related caches
            invalidateRelatedCaches(createdPost.getUserId());
            
            return createdPost;
        } catch (Exception e) {
            throw e;
        }
    }
    
    @Override
    public PostEntity update(int id, PostEntity post) throws DatabaseException, EntityNotFoundException, ValidationException {
        try {
            PostEntity updatedPost = super.update(id, post);
            
            if (updatedPost != null) {
                // Update cache
                postCache.put(id, updatedPost);
                postByTitleCache.put(updatedPost.getTitle(), updatedPost);
                
                // Invalidate related caches
                invalidateRelatedCaches(updatedPost.getUserId());
            }
            
            return updatedPost;
        } catch (Exception e) {
            throw e;
        }
    }
    
    @Override
    public boolean delete(int id) throws DatabaseException, EntityNotFoundException, ValidationException {
        try {
            // Get post before deletion to invalidate caches
            PostEntity postToDelete = findById(id);
            boolean deleted = super.delete(id);
            
            if (deleted) {
                // Remove from caches
                postCache.remove(id);
                postByTitleCache.remove(postToDelete.getTitle());
                
                // Invalidate related caches
                invalidateRelatedCaches(postToDelete.getUserId());
            }
            
            return deleted;
        } catch (Exception e) {
            throw e;
        }
    }
    
    @Override
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
            List<PostEntity> posts = super.findByUserId(userId);
            
            // Cache the result
            userPostsCache.put(userId, posts);
            
            return posts;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("POST_FIND_USER_ERROR", "Failed to find posts by user ID: " + userId, e);
        }
    }
    
    @Override
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
            List<PostEntity> allPosts = super.findAll();
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
