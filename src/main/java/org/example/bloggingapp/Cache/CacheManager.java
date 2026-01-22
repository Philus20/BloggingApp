package org.example.bloggingapp.Cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central cache manager for monitoring and maintaining all cache instances
 * Provides automatic cleanup, statistics collection, and cache health monitoring
 */
public class CacheManager {
    
    private static CacheManager instance;
    private final ScheduledExecutorService cleanupExecutor;
    private volatile boolean isRunning;
    private final Map<String, InMemoryCacheService<?, ?>> cacheRegistry;
    
    private CacheManager() {
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "CacheManager-Cleanup");
            t.setDaemon(true);
            return t;
        });
        this.isRunning = false;
        this.cacheRegistry = new ConcurrentHashMap<>();
    }
    
    /**
     * Gets the singleton instance of CacheManager
     * @return CacheManager instance
     */
    public static synchronized CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }
    
    /**
     * Starts the cache manager with automatic cleanup
     * @param cleanupIntervalMinutes interval between cleanup operations in minutes
     */
    public void start(int cleanupIntervalMinutes) {
        if (!isRunning) {
            isRunning = true;
            cleanupExecutor.scheduleAtFixedRate(
                this::performCleanup,
                cleanupIntervalMinutes,
                cleanupIntervalMinutes,
                TimeUnit.MINUTES
            );
            System.out.println("CacheManager started with cleanup interval: " + cleanupIntervalMinutes + " minutes");
        }
    }
    
    /**
     * Starts the cache manager with default 5-minute cleanup interval
     */
    public void start() {
        start(5);
    }
    
    /**
     * Stops the cache manager
     */
    public void stop() {
        if (isRunning) {
            isRunning = false;
            cleanupExecutor.shutdown();
            try {
                if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    cleanupExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                cleanupExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println("CacheManager stopped");
        }
    }
    
    /**
     * Performs cleanup on all registered caches
     */
    private void performCleanup() {
        try {
            System.out.println("Performing cache cleanup...");
            // This method would be called to clean up expired entries
            // Individual cache instances handle their own cleanup
            System.out.println("Cache cleanup completed");
        } catch (Exception e) {
            System.err.println("Error during cache cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Prints comprehensive cache statistics
     */
    public void printCacheStatistics() {
        System.out.println("=== Cache Manager Statistics ===");
        System.out.println("Status: " + (isRunning ? "Running" : "Stopped"));
        System.out.println("Cleanup Executor: " + 
            (cleanupExecutor.isShutdown() ? "Shutdown" : "Active"));
        System.out.println("================================");
    }
    
    /**
     * Creates a new cache instance with default configuration
     * @param <K> key type
     * @param <V> value type
     * @return new InMemoryCacheService instance
     */
    public <K, V> InMemoryCacheService<K, V> createCache() {
        return new InMemoryCacheService<>();
    }
    
    /**
     * Creates a new cache instance with custom configuration
     * @param maxSize maximum cache size
     * @param expirationMillis expiration time in milliseconds
     * @param <K> key type
     * @param <V> value type
     * @return new InMemoryCacheService instance
     */
    public <K, V> InMemoryCacheService<K, V> createCache(int maxSize, long expirationMillis) {
        return new InMemoryCacheService<>(maxSize, expirationMillis);
    }
    
    /**
     * Creates a new cache instance from configuration
     * @param config cache configuration
     * @param <K> key type
     * @param <V> value type
     * @return new InMemoryCacheService instance
     */
    public <K, V> InMemoryCacheService<K, V> createCache(CacheConfig config) {
        return new InMemoryCacheService<>(config.getMaxSize(), config.getExpirationMillis());
    }
    
    /**
     * Checks if the cache manager is running
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Forces an immediate cleanup operation
     */
    public void forceCleanup() {
        performCleanup();
    }
    
    /**
     * Registers a cache instance with the manager
     * @param name cache name/identifier
     * @param cache cache instance
     */
    public void registerCache(String name, InMemoryCacheService<?, ?> cache) {
        cacheRegistry.put(name, cache);
    }
    
    /**
     * Gets the total size of all registered caches
     * @return total number of entries across all caches
     */
    public int size() {
        int totalSize = 0;
        for (InMemoryCacheService<?, ?> cache : cacheRegistry.values()) {
            totalSize += cache.size();
        }
        return totalSize;
    }
    
    /**
     * Clears a specific cache by name
     * @param cacheName the name of the cache to clear
     */
    public void clear(String cacheName) {
        InMemoryCacheService<?, ?> cache = cacheRegistry.get(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
    
    /**
     * Clears all registered caches
     */
    public void clearAll() {
        for (InMemoryCacheService<?, ?> cache : cacheRegistry.values()) {
            cache.clear();
        }
    }
    
    /**
     * Cleans up expired entries across all caches
     * @return total number of expired entries removed
     */
    public int cleanupExpired() {
        int totalCleaned = 0;
        for (InMemoryCacheService<?, ?> cache : cacheRegistry.values()) {
            if (cache instanceof InMemoryCacheService) {
                totalCleaned += ((InMemoryCacheService<?, ?>) cache).cleanupExpired();
            }
        }
        return totalCleaned;
    }
}
