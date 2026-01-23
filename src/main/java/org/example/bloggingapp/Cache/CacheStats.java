package org.example.bloggingapp.Cache;

/**
 * Cache statistics for monitoring performance metrics
 */
public class CacheStats {
    private long hitCount;
    private long missCount;
    private long evictionCount;
    private long putCount;
    private long removalCount;
    
    public CacheStats() {
        this.hitCount = 0;
        this.missCount = 0;
        this.evictionCount = 0;
        this.putCount = 0;
        this.removalCount = 0;
    }
    
    public CacheStats(long hitCount, long missCount, long evictionCount, long putCount, long removalCount) {
        this.hitCount = hitCount;
        this.missCount = missCount;
        this.evictionCount = evictionCount;
        this.putCount = putCount;
        this.removalCount = removalCount;
    }
    
    /**
     * Returns the percentage of cache lookups that resulted in a hit
     * @return hit rate as a percentage (0.0 to 100.0)
     */
    public double getHitRate() {
        long totalRequests = hitCount + missCount;
        return totalRequests == 0 ? 0.0 : (double) hitCount / totalRequests * 100.0;
    }
    
    /**
     * Returns the percentage of cache lookups that resulted in a miss
     * @return miss rate as a percentage (0.0 to 100.0)
     */
    public double getMissRate() {
        long totalRequests = hitCount + missCount;
        return totalRequests == 0 ? 0.0 : (double) missCount / totalRequests * 100.0;
    }
    
    // Getters
    public long getHitCount() { return hitCount; }
    public long getMissCount() { return missCount; }
    public long getEvictionCount() { return evictionCount; }
    public long getPutCount() { return putCount; }
    public long getRemovalCount() { return removalCount; }
    
    // Setters for internal use
    void incrementHitCount() { this.hitCount++; }
    void incrementMissCount() { this.missCount++; }
    void incrementEvictionCount() { this.evictionCount++; }
    void incrementPutCount() { this.putCount++; }
    void incrementRemovalCount() { this.removalCount++; }
    
    void reset() {
        this.hitCount = 0;
        this.missCount = 0;
        this.evictionCount = 0;
        this.putCount = 0;
        this.removalCount = 0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "CacheStats{hits=%d, misses=%d, hitRate=%.2f%%, missRate=%.2f%%, evictions=%d, puts=%d, removals=%d}",
            hitCount, missCount, getHitRate(), getMissRate(), evictionCount, putCount, removalCount
        );
    }
}
