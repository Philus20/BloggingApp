package org.example.bloggingapp.Cache;

import org.example.bloggingapp.Database.Services.CachedPostService;
import org.example.bloggingapp.Database.Services.CachedUserService;
import org.example.bloggingapp.Database.Repositories.PostRepository;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Models.UserEntity;


/**
 * Demonstration class showing how to use the caching service
 */
public class CacheDemo {
    
    public static void main(String[] args) {
        // Initialize cache manager
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.start(1); // Cleanup every 1 minute for demo
        
        try {
            // Create cached services
            CachedPostService postService = new CachedPostService(new PostRepository());
            CachedUserService userService = new CachedUserService();
            
            // Demonstrate caching functionality
            demonstratePostCaching(postService);
            demonstrateUserCaching(userService);
            demonstrateCacheStatistics(postService, userService);
            
        } catch (Exception e) {
            System.err.println("Demo error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Stop cache manager
            cacheManager.stop();
        }
    }
    
    private static void demonstratePostCaching(CachedPostService postService) {
        System.out.println("\n=== Post Caching Demo ===");
        
        try {
            // Create a sample post
            PostEntity post = new PostEntity();
            post.setTitle("Sample Blog Post");
            post.setContent("This is a sample blog post content for demonstration.");
            post.setUserId(1);
            
            // Create post (will be cached)
            System.out.println("Creating post...");
            PostEntity createdPost = postService.create(post);
            System.out.println("Created post: " + createdPost.getTitle());
            
            // First retrieval (from cache)
            System.out.println("First retrieval (should be from cache)...");
            long startTime = System.nanoTime();
            PostEntity cachedPost1 = postService.findById(createdPost.getPostId());
            long duration1 = System.nanoTime() - startTime;
            System.out.println("Retrieved in: " + duration1 + " ns");
            
            // Second retrieval (from cache)
            System.out.println("Second retrieval (should be from cache)...");
            startTime = System.nanoTime();
            PostEntity cachedPost2 = postService.findById(createdPost.getPostId());
            long duration2 = System.nanoTime() - startTime;
            System.out.println("Retrieved in: " + duration2 + " ns");
            
            // Show performance improvement
            if (duration2 < duration1) {
                double improvement = ((double)(duration1 - duration2) / duration1) * 100;
                System.out.printf("Performance improvement: %.2f%%\n", improvement);
            }
            
        } catch (Exception e) {
            System.err.println("Post caching demo error: " + e.getMessage());
        }
    }
    
    private static void demonstrateUserCaching(CachedUserService userService) {
        System.out.println("\n=== User Caching Demo ===");
        
        try {
            // Create a sample user
            UserEntity user = new UserEntity();
            user.setUserName("demo_user");
            user.setEmail("demo@example.com");
            user.setPassword("hashed_password");
            
            // Create user (will be cached)
            System.out.println("Creating user...");
            UserEntity createdUser = userService.create(user);
            System.out.println("Created user: " + createdUser.getUserName());
            
            // Find by email (from cache)
            System.out.println("Finding user by email...");
            long startTime = System.nanoTime();
            UserEntity foundUser = userService.findByEmail("demo@example.com");
            long duration = System.nanoTime() - startTime;
            System.out.println("Found user in: " + duration + " ns");
            
            // Find by username (from cache)
            System.out.println("Finding user by username...");
            startTime = System.nanoTime();
            UserEntity foundByUsername = userService.findByUsername("demo_user");
            duration = System.nanoTime() - startTime;
            System.out.println("Found user in: " + duration + " ns");
            
        } catch (Exception e) {
            System.err.println("User caching demo error: " + e.getMessage());
        }
    }
    
    private static void demonstrateCacheStatistics(CachedPostService postService, CachedUserService userService) {
        System.out.println("\n=== Cache Statistics ===");
        
        // Show post cache statistics
        System.out.println(postService.getCacheStats());
        
        // Show user cache statistics
        System.out.println(userService.getCacheStats());
        
        // Show cache manager statistics
        CacheManager.getInstance().printCacheStatistics();
    }
    
    /**
     * Demonstrates cache configuration
     */
    public static void demonstrateCacheConfiguration() {
        System.out.println("\n=== Cache Configuration Demo ===");
        
        // Create custom cache configurations
        CacheConfig fastCache = CacheConfig.builder()
            .maxSize(100)
            .expirationMinutes(2)
            .enableStatistics(true)
            .enableCleanup(true)
            .cleanupInterval(1)
            .build();
        
        CacheConfig slowCache = CacheConfig.builder()
            .maxSize(1000)
            .expirationHours(1)
            .enableStatistics(true)
            .enableCleanup(false)
            .build();
        
        System.out.println("Fast Cache Config: " + fastCache);
        System.out.println("Slow Cache Config: " + slowCache);
        
        // Create caches with configurations
        CacheManager manager = CacheManager.getInstance();
        InMemoryCacheService<String, String> fastCacheInstance = manager.createCache(fastCache);
        InMemoryCacheService<String, String> slowCacheInstance = manager.createCache(slowCache);
        
        // Test the caches
        fastCacheInstance.put("test", "fast value");
        slowCacheInstance.put("test", "slow value");
        
        System.out.println("Fast cache hit: " + fastCacheInstance.get("test").orElse("Not found"));
        System.out.println("Slow cache hit: " + slowCacheInstance.get("test").orElse("Not found"));
        
        // Show statistics
        System.out.println("Fast cache stats: " + fastCacheInstance.getStats());
        System.out.println("Slow cache stats: " + slowCacheInstance.getStats());
    }
}
