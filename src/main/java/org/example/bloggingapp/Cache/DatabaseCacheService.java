package org.example.bloggingapp.Cache;

import org.example.bloggingapp.Database.DbInterfaces.CacheService;
import org.example.bloggingapp.Database.factories.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Database-backed cache implementation that persists cache entries to the database
 * Provides persistence across application restarts and distributed caching capabilities
 */
public class DatabaseCacheService<K, V> implements CacheService<K, V> {
    
    private final String tableName;
    private final ConnectionFactory connection;
    private final long defaultExpirationMillis;
    private final CacheStats stats;
    
    public DatabaseCacheService(String tableName) {
        this(tableName, TimeUnit.MINUTES.toMillis(30));
    }
    
    public DatabaseCacheService(String tableName, long defaultExpirationMillis) {
        this.tableName = tableName;
        this.defaultExpirationMillis = defaultExpirationMillis;
        this.connection = new org.example.bloggingapp.Database.factories.ConnectionFactory();
        this.stats = new CacheStats();
        initializeCacheTable();
    }
    
    /**
     * Creates the cache table if it doesn't exist
     */
    private void initializeCacheTable() {
        try (Connection conn = connection.createConnection()) {
            String createTableSQL = String.format("""
                CREATE TABLE IF NOT EXISTS %s (
                    cache_key VARCHAR(255) PRIMARY KEY,
                    cache_value TEXT NOT NULL,
                    expiration_time BIGINT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """, tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
                stmt.executeUpdate();
            }
            
            // Create index for expiration cleanup
            String createIndexSQL = String.format(
                "CREATE INDEX IF NOT EXISTS idx_%s_expiration ON %s (expiration_time)",
                tableName, tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(createIndexSQL)) {
                stmt.executeUpdate();
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize cache table: " + tableName, e);
        }
    }
    
    @Override
    public Optional<V> get(K key) {
        try (Connection conn = connection.createConnection()) {
            String sql = String.format(
                "SELECT cache_value, expiration_time FROM %s WHERE cache_key = ?", tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, key.toString());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        long expirationTime = rs.getLong("expiration_time");
                        
                        // Check if expired
                        if (expirationTime > 0 && System.currentTimeMillis() > expirationTime) {
                            remove(key);
                            stats.incrementMissCount();
                            return Optional.empty();
                        }
                        
                        // Update access time
                        updateAccessTime(key);
                        
                        // Deserialize and return value
                        String serializedValue = rs.getString("cache_value");
                        V value = deserializeValue(serializedValue);
                        
                        stats.incrementHitCount();
                        return Optional.of(value);
                    }
                }
            }
            
            stats.incrementMissCount();
            return Optional.empty();
            
        } catch (Exception e) {
            stats.incrementMissCount();
            throw new RuntimeException("Failed to get cache value for key: " + key, e);
        }
    }
    
    @Override
    public void put(K key, V value) {
        put(key, value, defaultExpirationMillis, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void put(K key, V value, long timeout, TimeUnit timeUnit) {
        try (Connection conn = connection.createConnection()) {
            long expirationTime = timeout > 0 ? System.currentTimeMillis() + timeUnit.toMillis(timeout) : 0;
            String serializedValue = serializeValue(value);
            
            String sql = String.format("""
                INSERT OR REPLACE INTO %s (cache_key, cache_value, expiration_time, accessed_at)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                """, tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, key.toString());
                stmt.setString(2, serializedValue);
                stmt.setLong(3, expirationTime);
                stmt.executeUpdate();
            }
            
            stats.incrementPutCount();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to put cache value for key: " + key, e);
        }
    }
    
    @Override
    public boolean remove(K key) {
        try (Connection conn = connection.createConnection()) {
            String sql = String.format("DELETE FROM %s WHERE cache_key = ?", tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, key.toString());
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    stats.incrementRemovalCount();
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove cache value for key: " + key, e);
        }
    }
    
    @Override
    public void clear() {
        try (Connection conn = connection.createConnection()) {
            String sql = String.format("DELETE FROM %s", tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear cache table: " + tableName, e);
        }
    }
    
    @Override
    public int size() {
        try (Connection conn = connection.createConnection()) {
            String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
            return 0;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to get cache size for table: " + tableName, e);
        }
    }
    
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    
    @Override
    public boolean containsKey(K key) {
        try (Connection conn = connection.createConnection()) {
            String sql = String.format(
                "SELECT COUNT(*) FROM %s WHERE cache_key = ? AND (expiration_time = 0 OR expiration_time > ?)",
                tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, key.toString());
                stmt.setLong(2, System.currentTimeMillis());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public CacheStats getStats() {
        return new CacheStats(stats.getHitCount(), stats.getMissCount(), 
                            stats.getEvictionCount(), stats.getPutCount(), 
                            stats.getRemovalCount());
    }
    
    @Override
    public void resetStats() {
        stats.reset();
    }
    
    /**
     * Updates the access time for a cache entry
     */
    private void updateAccessTime(K key) {
        try (Connection conn = connection.createConnection()) {
            String sql = String.format(
                "UPDATE %s SET accessed_at = CURRENT_TIMESTAMP WHERE cache_key = ?", tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, key.toString());
                stmt.executeUpdate();
            }
            
        } catch (Exception e) {
            // Log error but don't fail the get operation
            System.err.println("Failed to update access time for key: " + key);
        }
    }
    
    /**
     * Serializes a value to a string for database storage
     */
    @SuppressWarnings("unchecked")
    private String serializeValue(V value) {
        if (value == null) {
            return "null";
        }
        
        // Simple serialization - in a real implementation, you might use JSON or Java serialization
        if (value instanceof String) {
            return "string:" + value;
        } else if (value instanceof Integer) {
            return "int:" + value;
        } else if (value instanceof Long) {
            return "long:" + value;
        } else if (value instanceof Boolean) {
            return "bool:" + value;
        } else {
            // For complex objects, you might use a JSON library
            return "object:" + value.toString();
        }
    }
    
    /**
     * Deserializes a value from a string
     */
    @SuppressWarnings("unchecked")
    private V deserializeValue(String serializedValue) {
        if (serializedValue == null || "null".equals(serializedValue)) {
            return null;
        }
        
        if (serializedValue.startsWith("string:")) {
            return (V) serializedValue.substring(7);
        } else if (serializedValue.startsWith("int:")) {
            return (V) Integer.valueOf(serializedValue.substring(4));
        } else if (serializedValue.startsWith("long:")) {
            return (V) Long.valueOf(serializedValue.substring(5));
        } else if (serializedValue.startsWith("bool:")) {
            return (V) Boolean.valueOf(serializedValue.substring(5));
        } else {
            // For complex objects, you might use a JSON library
            throw new UnsupportedOperationException("Complex object deserialization not implemented");
        }
    }
    
    /**
     * Removes all expired entries from the cache
     */
    public int cleanupExpired() {
        try (Connection conn = connection.createConnection()) {
            String sql = String.format(
                "DELETE FROM %s WHERE expiration_time > 0 AND expiration_time < ?", tableName);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, System.currentTimeMillis());
                int removedCount = stmt.executeUpdate();
                
                stats.incrementRemovalCount();
                return removedCount;
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to cleanup expired cache entries", e);
        }
    }
    
    /**
     * Gets the table name for this cache
     */
    public String getTableName() {
        return tableName;
    }
    
    /**
     * Gets the default expiration time in milliseconds
     */
    public long getDefaultExpirationMillis() {
        return defaultExpirationMillis;
    }
}
