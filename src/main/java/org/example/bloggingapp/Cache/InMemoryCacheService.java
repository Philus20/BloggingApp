package org.example.bloggingapp.Cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * In-memory cache implementation with LRU (Least Recently Used) eviction policy
 * Thread-safe implementation using ReadWriteLock for concurrent access
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 */
public class InMemoryCacheService<K, V> implements CacheService<K, V> {
    
    private final int maxSize;
    private final long defaultExpirationMillis;
    private final Map<K, CacheEntry<V>> cacheMap;
    private final ReadWriteLock lock;
    private final CacheStats stats;
    
    /**
     * Cache entry with value and expiration time
     */
    private static class CacheEntry<V> {
        private final V value;
        private final long expirationTime;
        
        public CacheEntry(V value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }
        
        public V getValue() { return value; }
        
        public boolean isExpired() {
            return expirationTime > 0 && System.currentTimeMillis() > expirationTime;
        }
    }
    
    /**
     * Creates a cache with default settings (1000 max entries, 5 minutes expiration)
     */
    public InMemoryCacheService() {
        this(1000, TimeUnit.MINUTES.toMillis(5));
    }
    
    /**
     * Creates a cache with specified maximum size and default expiration time
     * @param maxSize maximum number of entries in the cache
     * @param defaultExpirationMillis default expiration time in milliseconds (0 for no expiration)
     */
    public InMemoryCacheService(int maxSize, long defaultExpirationMillis) {
        this.maxSize = maxSize;
        this.defaultExpirationMillis = defaultExpirationMillis;
        this.lock = new ReentrantReadWriteLock();
        this.stats = new CacheStats();
        
        // Create LinkedHashMap with access-order and LRU removal
        this.cacheMap = new LinkedHashMap<K, CacheEntry<V>>(maxSize + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                boolean shouldRemove = size() > maxSize;
                if (shouldRemove) {
                    stats.incrementEvictionCount();
                }
                return shouldRemove;
            }
        };
    }
    
    @Override
    public Optional<V> get(K key) {
        lock.readLock().lock();
        try {
            CacheEntry<V> entry = cacheMap.get(key);
            if (entry == null) {
                stats.incrementMissCount();
                return Optional.empty();
            }
            
            if (entry.isExpired()) {
                // Remove expired entry
                lock.readLock().unlock();
                lock.writeLock().lock();
                try {
                    cacheMap.remove(key);
                    stats.incrementRemovalCount();
                } finally {
                    lock.writeLock().unlock();
                    lock.readLock().lock();
                }
                stats.incrementMissCount();
                return Optional.empty();
            }
            
            stats.incrementHitCount();
            return Optional.of(entry.getValue());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void put(K key, V value) {
        put(key, value, defaultExpirationMillis, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void put(K key, V value, long timeout, TimeUnit timeUnit) {
        long expirationTime = timeout > 0 ? System.currentTimeMillis() + timeUnit.toMillis(timeout) : 0;
        
        lock.writeLock().lock();
        try {
            cacheMap.put(key, new CacheEntry<>(value, expirationTime));
            stats.incrementPutCount();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean remove(K key) {
        lock.writeLock().lock();
        try {
            CacheEntry<V> removed = cacheMap.remove(key);
            if (removed != null) {
                stats.incrementRemovalCount();
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            cacheMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return cacheMap.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return cacheMap.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public boolean containsKey(K key) {
        lock.readLock().lock();
        try {
            CacheEntry<V> entry = cacheMap.get(key);
            return entry != null && !entry.isExpired();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public CacheStats getStats() {
        lock.readLock().lock();
        try {
            return new CacheStats(stats.getHitCount(), stats.getMissCount(), 
                                stats.getEvictionCount(), stats.getPutCount(), 
                                stats.getRemovalCount());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void resetStats() {
        lock.writeLock().lock();
        try {
            stats.reset();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Removes all expired entries from the cache
     * @return the number of entries removed
     */
    public int cleanupExpired() {
        lock.writeLock().lock();
        try {
            int removedCount = 0;
            var iterator = cacheMap.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (entry.getValue().isExpired()) {
                    iterator.remove();
                    removedCount++;
                    stats.incrementRemovalCount();
                }
            }
            return removedCount;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Returns the maximum size of the cache
     * @return maximum number of entries
     */
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * Returns the default expiration time in milliseconds
     * @return default expiration time (0 means no expiration)
     */
    public long getDefaultExpirationMillis() {
        return defaultExpirationMillis;
    }
}
