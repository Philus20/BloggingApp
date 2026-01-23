package org.example.bloggingapp.Cache;

/**
 * Configuration class for cache instances
 * Contains settings for cache size, expiration, and other parameters
 */
public class CacheConfig {
    
    private final int maxSize;
    private final long expirationMillis;
    private final boolean enableStats;
    private final String name;
    
    /**
     * Default constructor with sensible defaults
     */
    public CacheConfig() {
        this(1000, 300000, true, "default"); // 1000 items, 5 minutes, stats enabled
    }
    
    /**
     * Constructor with custom size and expiration
     * @param maxSize maximum number of items in cache
     * @param expirationMillis expiration time in milliseconds
     */
    public CacheConfig(int maxSize, long expirationMillis) {
        this(maxSize, expirationMillis, true, "custom");
    }
    
    /**
     * Full constructor with all parameters
     * @param maxSize maximum number of items in cache
     * @param expirationMillis expiration time in milliseconds
     * @param enableStats whether to enable statistics collection
     * @param name cache name/identifier
     */
    public CacheConfig(int maxSize, long expirationMillis, boolean enableStats, String name) {
        this.maxSize = maxSize;
        this.expirationMillis = expirationMillis;
        this.enableStats = enableStats;
        this.name = name;
    }
    
    /**
     * Gets the maximum cache size
     * @return maximum number of items
     */
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * Gets the expiration time in milliseconds
     * @return expiration time
     */
    public long getExpirationMillis() {
        return expirationMillis;
    }
    
    /**
     * Checks if statistics are enabled
     * @return true if stats enabled, false otherwise
     */
    public boolean isEnableStats() {
        return enableStats;
    }
    
    /**
     * Gets the cache name
     * @return cache name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Creates a builder for CacheConfig
     * @return new CacheConfigBuilder instance
     */
    public static CacheConfigBuilder builder() {
        return new CacheConfigBuilder();
    }
    
    /**
     * Builder class for CacheConfig
     */
    public static class CacheConfigBuilder {
        private int maxSize = 1000;
        private long expirationMillis = 300000; // 5 minutes
        private boolean enableStats = true;
        private String name = "default";
        
        public CacheConfigBuilder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }
        
        public CacheConfigBuilder expiration(long expirationMillis) {
            this.expirationMillis = expirationMillis;
            return this;
        }
        
        public CacheConfigBuilder enableStats(boolean enableStats) {
            this.enableStats = enableStats;
            return this;
        }
        
        public CacheConfigBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public CacheConfig build() {
            return new CacheConfig(maxSize, expirationMillis, enableStats, name);
        }
    }
    
    @Override
    public String toString() {
        return "CacheConfig{" +
                "maxSize=" + maxSize +
                ", expirationMillis=" + expirationMillis +
                ", enableStats=" + enableStats +
                ", name='" + name + '\'' +
                '}';
    }
}
