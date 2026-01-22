# Caching Architecture Documentation

## Overview

The BloggingApp implements a sophisticated multi-layered caching system that stores real database values in memory for high-performance access. The caching architecture is designed to reduce database load while maintaining data consistency and providing fast response times.

## Core Components

### 1. CacheService Interface
**Location**: `org.example.bloggingapp.Database.DbInterfaces.CacheService`

The `CacheService<K, V>` interface defines the contract for all cache implementations in the system. It provides a generic API for key-value caching with support for expiration, statistics, and concurrent access.

**Key Methods**:
- `Optional<V> get(K key)` - Retrieve cached value
- `void put(K key, V value)` - Store value with default expiration
- `void put(K key, V value, long timeout, TimeUnit timeUnit)` - Store with custom expiration
- `boolean remove(K key)` - Remove specific entry
- `void clear()` - Clear all entries
- `CacheStats getStats()` - Get performance statistics

### 2. InMemoryCacheService Implementation
**Location**: `org.example.bloggingapp.Cache.InMemoryCacheService`

The primary cache implementation that stores data in memory using a thread-safe LRU (Least Recently Used) eviction policy.

**Features**:
- **Thread Safety**: Uses `ReadWriteLock` for concurrent access
- **LRU Eviction**: Automatically removes least recently used entries when capacity is reached
- **Expiration Support**: Time-based expiration of cache entries
- **Statistics Tracking**: Hit/miss ratios, eviction counts, and performance metrics
- **Automatic Cleanup**: Periodic removal of expired entries

**Configuration**:
```java
// Example cache configurations
InMemoryCacheService<Integer, PostEntity> postCache = 
    new InMemoryCacheService<>(500, 10 * 60 * 1000); // 500 entries, 10 minutes

InMemoryCacheService<String, UserEntity> userCache = 
    new InMemoryCacheService<>(1000, 15 * 60 * 1000); // 1000 entries, 15 minutes
```

### 3. DatabaseCacheService (Optional)
**Location**: `org.example.bloggingapp.Cache.DatabaseCacheService`

A database-backed cache implementation that provides persistence across application restarts. This can be used for distributed caching scenarios where cache persistence is required.

**Features**:
- **Database Persistence**: Cache entries stored in database tables
- **Cross-Application Sharing**: Multiple application instances can share cache
- **Automatic Table Management**: Creates cache tables and indexes automatically
- **Serialization Support**: Handles different data types with custom serialization

### 4. CacheManager
**Location**: `org.example.bloggingapp.Cache.CacheManager`

Centralized cache management singleton that coordinates all cache instances in the application.

**Responsibilities**:
- **Cache Registry**: Maintains registry of all active caches
- **Lifecycle Management**: Starts/stops cleanup operations
- **Statistics Aggregation**: Provides unified cache statistics
- **Bulk Operations**: Clear all caches, force cleanup operations

**Usage**:
```java
CacheManager manager = CacheManager.getInstance();
manager.start(5); // Start with 5-minute cleanup interval
manager.registerCache("posts", postCache);
manager.printCacheStatistics();
```

### 5. CacheStats
**Location**: `org.example.bloggingapp.Cache.CacheStats`

Collects and provides performance metrics for cache operations.

**Metrics Tracked**:
- Hit count and hit rate
- Miss count and miss rate
- Put operations count
- Removal operations count
- Eviction count

## Service Layer Integration

### CachedPostService
**Location**: `org.example.bloggingapp.Services.CachedPostService`

Extends `PostService` with intelligent caching for post-related operations.

**Cache Strategy**:
- **Individual Posts**: `CacheService<Integer, PostEntity>` - 500 entries, 10 minutes
- **Posts by Title**: `CacheService<String, PostEntity>` - 200 entries, 15 minutes
- **User Posts**: `CacheService<Integer, List<PostEntity>>` - 100 users, 5 minutes
- **All Posts**: `CacheService<String, List<PostEntity>>` - 10 lists, 2 minutes

**Key Features**:
- **Pre-population**: Loads all posts from database during initialization
- **Multi-key Caching**: Same post cached by ID, title, and user association
- **Cache Invalidation**: Smart invalidation of related caches on updates/deletes
- **Real Database Values**: Always caches actual database state, not computed values

### CachedUserService
**Location**: `org.example.bloggingapp.Services.CachedUserService`

Implements caching for user-related operations with multiple lookup strategies.

**Cache Strategy**:
- **Individual Users**: `CacheService<Integer, UserEntity>` - 1000 entries, 15 minutes
- **Users by Email**: `CacheService<String, UserEntity>` - 500 entries, 20 minutes
- **Users by Username**: `CacheService<String, UserEntity>` - 500 entries, 20 minutes
- **All Users**: `CacheService<String, List<UserEntity>>` - 5 lists, 5 minutes

**Key Features**:
- **Multiple Lookup Keys**: Same user cached by ID, email, and username
- **Pre-population**: Loads all users during service initialization
- **Consistent Caching**: Ensures all cache keys stay synchronized

### PostService (Base Service)
**Location**: `org.example.bloggingapp.Services.PostService`

The base service also includes caching capabilities and can be used directly when inheritance is preferred over composition.

## Cache Population Strategy

### Pre-population from Database
Both `CachedPostService` and `CachedUserService` implement pre-population strategies that load real database values into memory during service initialization:

```java
private void prepopulateCacheFromDatabase() {
    try {
        // Load all entities from database
        List<PostEntity> allPosts = postRepository.findAll();
        
        // Cache individual posts
        for (PostEntity post : allPosts) {
            postCache.put(post.getPostId(), post);
            postByTitleCache.put(post.getTitle(), post);
            // ... additional caching
        }
    } catch (Exception e) {
        // Graceful degradation if pre-population fails
    }
}
```

**Benefits**:
- **Warm Cache**: Application starts with cache already populated
- **Reduced Database Load**: Initial requests hit cache instead of database
- **Better Performance**: No cold start penalty for common operations

## Cache Invalidation Strategy

### Smart Invalidation
The caching system implements intelligent invalidation to maintain data consistency:

1. **Entity-level Invalidation**: When an entity is updated/deleted, all related cache entries are removed
2. **Collection Invalidation**: When any post is created/updated/deleted, collection caches (like "all posts") are invalidated
3. **Dependency Invalidation**: Changes to user data invalidate related post caches

### Example Invalidation Logic
```java
private void invalidateRelatedCaches(int userId) {
    allPostsCache.remove("all");           // Invalidate all posts list
    userPostsCache.remove(userId);         // Invalidate user-specific posts
}
```

## Performance Monitoring

### Cache Statistics
Each cache instance provides detailed statistics for monitoring performance:

```java
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
```

### CacheManager Statistics
The `CacheManager` provides aggregated statistics across all registered caches:

```java
CacheManager.getInstance().printCacheStatistics();
```

## Usage Examples

### Basic Service Usage
```java
// Create cached services
CachedPostService postService = new CachedPostService(new PostRepository());
CachedUserService userService = new CachedUserService();

// Use services - caching is transparent
PostEntity post = postService.findById(1);  // First call: database, then cache
PostEntity post2 = postService.findById(1); // Second call: cache only

UserEntity user = userService.findByEmail("user@example.com");
```

### Cache Management
```java
// Get cache statistics
System.out.println(postService.getCacheStats());

// Clear all caches
postService.clearAllCaches();
userService.clearAllCaches();

// Force cleanup of expired entries
CacheManager.getInstance().forceCleanup();
```

### Custom Cache Configuration
```java
// Create custom cache configuration
CacheConfig config = CacheConfig.builder()
    .maxSize(1000)
    .expirationMinutes(30)
    .enableStatistics(true)
    .enableCleanup(true)
    .cleanupInterval(5)
    .build();

// Create cache with custom configuration
InMemoryCacheService<String, Object> customCache = 
    CacheManager.getInstance().createCache(config);
```

## Best Practices

### 1. Cache Configuration
- **Size Limits**: Set appropriate cache sizes based on available memory
- **Expiration Times**: Use shorter expiration for frequently changing data
- **Monitoring**: Enable statistics for performance tuning

### 2. Cache Invalidation
- **Granular Invalidation**: Invalidate only affected cache entries
- **Consistency**: Ensure all cache keys for the same entity stay synchronized
- **Dependency Management**: Consider cross-entity relationships

### 3. Error Handling
- **Graceful Degradation**: Continue operation even if cache fails
- **Logging**: Log cache-related errors for monitoring
- **Fallback**: Always have database as the source of truth

### 4. Performance Optimization
- **Pre-population**: Load frequently accessed data during startup
- **Batch Operations**: Use bulk operations where possible
- **Memory Management**: Monitor memory usage and adjust cache sizes

## Thread Safety

All cache implementations are thread-safe and designed for concurrent access:

- **ReadWriteLock**: Allows multiple readers, exclusive writers
- **Atomic Operations**: Cache operations are atomic
- **Consistent State**: No partial updates or race conditions

## Integration Points

### Database Integration
The caching system integrates seamlessly with the existing database layer:

- **Repository Pattern**: Works with existing repository implementations
- **Connection Management**: Uses existing connection factories
- **Transaction Safety**: Cache updates are coordinated with database transactions

### Service Layer Integration
Caching is integrated at the service layer for business logic transparency:

- **Transparent Caching**: Service consumers don't need to know about caching
- **Consistent API**: Same interface as non-cached services
- **Inheritance Support**: Can extend existing services with caching

## Conclusion

The BloggingApp caching architecture provides a robust, high-performance solution for storing real database values in memory. The multi-layered approach with intelligent invalidation, comprehensive monitoring, and seamless database integration ensures optimal performance while maintaining data consistency.

The system is designed to be:
- **Performant**: Fast in-memory access with intelligent pre-population
- **Reliable**: Thread-safe with graceful error handling
- **Scalable**: Configurable cache sizes and expiration policies
- **Maintainable**: Clear separation of concerns and comprehensive monitoring
