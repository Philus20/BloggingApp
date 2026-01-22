package org.example.bloggingapp.Cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Generic in-memory caching service interface for fast data access
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 */
public interface CacheService<K, V> {
    
    /**
     * Retrieves a value from the cache
     * @param key the key whose associated value is to be returned
     * @return Optional containing the cached value, or empty if not present or expired
     */
    Optional<V> get(K key);
    
    /**
     * Associates the specified value with the specified key in the cache
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    void put(K key, V value);
    
    /**
     * Associates the specified value with the specified key in the cache with expiration
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param timeout the expiration time
     * @param timeUnit the time unit of the timeout parameter
     */
    void put(K key, V value, long timeout, TimeUnit timeUnit);
    
    /**
     * Removes the mapping for a key from this cache if present
     * @param key key whose mapping is to be removed from the cache
     * @return true if the key was removed, false if key was not present
     */
    boolean remove(K key);
    
    /**
     * Removes all mappings from the cache
     */
    void clear();
    
    /**
     * Returns the current size of the cache
     * @return the number of key-value mappings in this cache
     */
    int size();
    
    /**
     * Returns true if this cache contains no key-value mappings
     * @return true if cache is empty, false otherwise
     */
    boolean isEmpty();
    
    /**
     * Returns true if this cache contains a mapping for the specified key
     * @param key key whose presence in this cache is to be tested
     * @return true if cache contains the key, false otherwise
     */
    boolean containsKey(K key);
    
    /**
     * Returns cache statistics including hit rate, miss rate, and eviction count
     * @return CacheStats object containing performance metrics
     */
    CacheStats getStats();
    
    /**
     * Resets cache statistics
     */
    void resetStats();
}
