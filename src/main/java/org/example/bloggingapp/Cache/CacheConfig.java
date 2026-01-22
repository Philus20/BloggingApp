package org.example.bloggingapp.Cache;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for cache settings
 */
public class CacheConfig {
    
    private final int maxSize;
    private final long expirationTime;
    private final TimeUnit timeUnit;
    private final boolean enableStatistics;
    private final boolean enableCleanup;
    private final long cleanupInterval;
    
    private CacheConfig(Builder builder) {
        this.maxSize = builder.maxSize;
        this.expirationTime = builder.expirationTime;
        this.timeUnit = builder.timeUnit;
        this.enableStatistics = builder.enableStatistics;
        this.enableCleanup = builder.enableCleanup;
        this.cleanupInterval = builder.cleanupInterval;
    }
    
    /**
     * Creates a new builder for CacheConfig
     * @return new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a default configuration
     * @return default CacheConfig with 1000 max size and 5 minutes expiration
     */
    public static CacheConfig defaultConfig() {
        return builder().build();
    }
    
    // Getters
    public int getMaxSize() { return maxSize; }
    public long getExpirationTime() { return expirationTime; }
    public TimeUnit getTimeUnit() { return timeUnit; }
    public boolean isStatisticsEnabled() { return enableStatistics; }
    public boolean isCleanupEnabled() { return enableCleanup; }
    public long getCleanupInterval() { return cleanupInterval; }
    
    /**
     * Returns expiration time in milliseconds
     * @return expiration time in milliseconds
     */
    public long getExpirationMillis() {
        return timeUnit.toMillis(expirationTime);
    }
    
    /**
     * Builder class for CacheConfig
     */
    public static class Builder {
        private int maxSize = 1000;
        private long expirationTime = 5;
        private TimeUnit timeUnit = TimeUnit.MINUTES;
        private boolean enableStatistics = true;
        private boolean enableCleanup = true;
        private long cleanupInterval = 1; // in minutes
        
        /**
         * Sets maximum cache size
         * @param maxSize maximum number of entries
         * @return Builder instance
         */
        public Builder maxSize(int maxSize) {
            if (maxSize <= 0) {
                throw new IllegalArgumentException("Max size must be positive");
            }
            this.maxSize = maxSize;
            return this;
        }
        
        /**
         * Sets expiration time
         * @param expirationTime expiration time value
         * @param timeUnit time unit for expiration
         * @return Builder instance
         */
        public Builder expiration(long expirationTime, TimeUnit timeUnit) {
            if (expirationTime < 0) {
                throw new IllegalArgumentException("Expiration time cannot be negative");
            }
            this.expirationTime = expirationTime;
            this.timeUnit = timeUnit;
            return this;
        }
        
        /**
         * Sets expiration time in minutes
         * @param minutes expiration time in minutes
         * @return Builder instance
         */
        public Builder expirationMinutes(long minutes) {
            return expiration(minutes, TimeUnit.MINUTES);
        }
        
        /**
         * Sets expiration time in seconds
         * @param seconds expiration time in seconds
         * @return Builder instance
         */
        public Builder expirationSeconds(long seconds) {
            return expiration(seconds, TimeUnit.SECONDS);
        }
        
        /**
         * Sets expiration time in hours
         * @param hours expiration time in hours
         * @return Builder instance
         */
        public Builder expirationHours(long hours) {
            return expiration(hours, TimeUnit.HOURS);
        }
        
        /**
         * Enables or disables statistics collection
         * @param enableStatistics true to enable statistics
         * @return Builder instance
         */
        public Builder enableStatistics(boolean enableStatistics) {
            this.enableStatistics = enableStatistics;
            return this;
        }
        
        /**
         * Enables or disables automatic cleanup of expired entries
         * @param enableCleanup true to enable cleanup
         * @return Builder instance
         */
        public Builder enableCleanup(boolean enableCleanup) {
            this.enableCleanup = enableCleanup;
            return this;
        }
        
        /**
         * Sets cleanup interval in minutes
         * @param cleanupInterval cleanup interval in minutes
         * @return Builder instance
         */
        public Builder cleanupInterval(long cleanupInterval) {
            if (cleanupInterval <= 0) {
                throw new IllegalArgumentException("Cleanup interval must be positive");
            }
            this.cleanupInterval = cleanupInterval;
            return this;
        }
        
        /**
         * Builds the CacheConfig instance
         * @return CacheConfig instance
         */
        public CacheConfig build() {
            return new CacheConfig(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format(
            "CacheConfig{maxSize=%d, expiration=%d %s, statistics=%s, cleanup=%s, cleanupInterval=%d minutes}",
            maxSize, expirationTime, timeUnit, enableStatistics, enableCleanup, cleanupInterval
        );
    }
}
