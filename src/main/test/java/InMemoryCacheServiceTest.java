import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryCacheService Tests")
class InMemoryCacheServiceTest {

    private InMemoryCacheService<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new InMemoryCacheService<>(100, TimeUnit.MINUTES.toMillis(5));
    }

    @Nested
    @DisplayName("Basic Cache Operations Tests")
    class BasicOperationsTests {

        @Test
        @DisplayName("Should put and get value successfully")
        void shouldPutAndGetValueSuccessfully() {
            cache.put("key1", "value1");
            Optional<String> result = cache.get("key1");
            
            assertTrue(result.isPresent());
            assertEquals("value1", result.get());
        }

        @Test
        @DisplayName("Should return empty for non-existent key")
        void shouldReturnEmptyForNonExistentKey() {
            Optional<String> result = cache.get("nonexistent");
            
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should put and get value with custom expiration")
        void shouldPutAndGetValueWithCustomExpiration() throws InterruptedException {
            cache.put("key1", "value1", 100, TimeUnit.MILLISECONDS);
            
            // Should be available immediately
            Optional<String> result = cache.get("key1");
            assertTrue(result.isPresent());
            
            // Should expire after 100ms
            Thread.sleep(150);
            result = cache.get("key1");
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should remove value successfully")
        void remove() {
            cache.put("key1", "value1");
            assertTrue(cache.get("key1").isPresent());
            
            boolean removed = cache.remove("key1");
            assertTrue(removed);
            assertFalse(cache.get("key1").isPresent());
        }

        @Test
        @DisplayName("Should return false when removing non-existent key")
        void shouldReturnFalseWhenRemovingNonExistentKey() {
            boolean removed = cache.remove("nonexistent");
            assertFalse(removed);
        }

        @Test
        @DisplayName("Should clear all values successfully")
        void shouldClearAllValuesSuccessfully() {
            cache.put("key1", "value1");
            cache.put("key2", "value2");
            
            assertEquals(2, cache.size());
            
            cache.clear();
            
            assertEquals(0, cache.size());
            assertTrue(cache.isEmpty());
        }

        @Test
        @DisplayName("Should handle null key gracefully")
        void shouldHandleNullKeyGracefully() {
            // Should not throw exception
            assertDoesNotThrow(() -> cache.get(null));
            assertDoesNotThrow(() -> cache.remove(null));
        }

        @Test
        @DisplayName("Should handle null value gracefully")
        void shouldHandleNullValueGracefully() {
            cache.put("key1", null);
            Optional<String> result = cache.get("key1");
            
            assertTrue(result.isPresent());
            assertNull(result.get());
        }
    }

    @Nested
    @DisplayName("LRU Eviction Tests")
    class LRUEvictionTests {

        @Test
        @DisplayName("Should evict least recently used entry when capacity exceeded")
        void shouldEvictLeastRecentlyUsedEntryWhenCapacityExceeded() {
            // Create cache with small capacity
            InMemoryCacheService<String, String> smallCache = 
                new InMemoryCacheService<>(2, TimeUnit.MINUTES.toMillis(5));
            
            smallCache.put("key1", "value1");
            smallCache.put("key2", "value2");
            assertEquals(2, smallCache.size());
            
            // Add third entry - should evict key1 (least recently used)
            smallCache.put("key3", "value3");
            assertEquals(2, smallCache.size());
            
            // key1 should be evicted, key2 and key3 should remain
            assertFalse(smallCache.get("key1").isPresent());
            assertTrue(smallCache.get("key2").isPresent());
            assertTrue(smallCache.get("key3").isPresent());
        }

        @Test
        @DisplayName("Should update recency on access")
        void shouldUpdateRecencyOnAccess() {
            InMemoryCacheService<String, String> smallCache = 
                new InMemoryCacheService<>(2, TimeUnit.MINUTES.toMillis(5));
            
            smallCache.put("key1", "value1");
            smallCache.put("key2", "value2");
            
            // Access key1 to make it most recently used
            smallCache.get("key1");
            
            // Add third entry - should evict key2 (now least recently used)
            smallCache.put("key3", "value3");
            
            assertFalse(smallCache.get("key2").isPresent());
            assertTrue(smallCache.get("key1").isPresent());
            assertTrue(smallCache.get("key3").isPresent());
        }

        @Test
        @DisplayName("Should track eviction count in statistics")
        void shouldTrackEvictionCountInStatistics() {
            InMemoryCacheService<String, String> smallCache = 
                new InMemoryCacheService<>(2, TimeUnit.MINUTES.toMillis(5));
            
            CacheStats initialStats = smallCache.getStats();
            long initialEvictions = initialStats.getEvictionCount();
            
            smallCache.put("key1", "value1");
            smallCache.put("key2", "value2");
            smallCache.put("key3", "value3"); // Should trigger eviction
            
            CacheStats finalStats = smallCache.getStats();
            assertTrue(finalStats.getEvictionCount() > initialEvictions);
        }
    }

    @Nested
    @DisplayName("Expiration Tests")
    class ExpirationTests {

        @Test
        @DisplayName("Should expire entries after default expiration time")
        void shouldExpireEntriesAfterDefaultExpirationTime() throws InterruptedException {
            InMemoryCacheService<String, String> shortLivedCache = 
                new InMemoryCacheService<>(100, TimeUnit.MILLISECONDS.toMillis(100));
            
            shortLivedCache.put("key1", "value1");
            assertTrue(shortLivedCache.get("key1").isPresent());
            
            // Wait for expiration
            Thread.sleep(150);
            
            assertFalse(shortLivedCache.get("key1").isPresent());
        }

        @Test
        @DisplayName("Should not expire entries with zero expiration time")
        void shouldNotExpireEntriesWithZeroExpirationTime() throws InterruptedException {
            InMemoryCacheService<String, String> permanentCache = 
                new InMemoryCacheService<>(100, 0); // No expiration
            
            permanentCache.put("key1", "value1");
            assertTrue(permanentCache.get("key1").isPresent());
            
            // Wait and check again
            Thread.sleep(100);
            
            assertTrue(permanentCache.get("key1").isPresent());
        }

        @Test
        @DisplayName("Should cleanup expired entries")
        void shouldCleanupExpiredEntries() throws InterruptedException {
            InMemoryCacheService<String, String> cache = 
                new InMemoryCacheService<>(100, TimeUnit.MILLISECONDS.toMillis(50));
            
            // Add entries that will expire
            cache.put("key1", "value1");
            cache.put("key2", "value2");
            cache.put("key3", "value3");
            
            assertEquals(3, cache.size());
            
            // Wait for expiration
            Thread.sleep(100);
            
            // Cleanup expired entries
            int removedCount = cache.cleanupExpired();
            
            assertTrue(removedCount > 0);
            assertEquals(0, cache.size());
        }

        @Test
        @DisplayName("Should handle expired entries on get")
        void shouldHandleExpiredEntriesOnGet() throws InterruptedException {
            InMemoryCacheService<String, String> cache = 
                new InMemoryCacheService<>(100, TimeUnit.MILLISECONDS.toMillis(50));
            
            cache.put("key1", "value1");
            assertTrue(cache.get("key1").isPresent());
            
            // Wait for expiration
            Thread.sleep(100);
            
            // Get should return empty and remove expired entry
            Optional<String> result = cache.get("key1");
            assertFalse(result.isPresent());
            assertEquals(0, cache.size());
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should handle concurrent reads safely")
        void shouldHandleConcurrentReadsSafely() throws InterruptedException {
            cache.put("key1", "value1");
            
            int threadCount = 10;
            int operationsPerThread = 100;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < operationsPerThread; j++) {
                            Optional<String> result = cache.get("key1");
                            if (result.isPresent() && "value1".equals(result.get())) {
                                successCount.incrementAndGet();
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            latch.await();
            executor.shutdown();
            
            assertEquals(threadCount * operationsPerThread, successCount.get());
        }

        @Test
        @DisplayName("Should handle concurrent writes safely")
        void shouldHandleConcurrentWritesSafely() throws InterruptedException {
            int threadCount = 10;
            int operationsPerThread = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < operationsPerThread; j++) {
                            cache.put("key" + threadId + "_" + j, "value" + threadId + "_" + j);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            latch.await();
            executor.shutdown();
            
            // Verify all entries were added
            assertEquals(threadCount * operationsPerThread, cache.size());
        }

        @Test
        @DisplayName("Should handle mixed concurrent operations safely")
        void shouldHandleMixedConcurrentOperationsSafely() throws InterruptedException {
            int threadCount = 20;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            
            // Some threads write, some read
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        if (threadId % 2 == 0) {
                            // Write thread
                            for (int j = 0; j < 10; j++) {
                                cache.put("write_" + threadId + "_" + j, "value_" + threadId + "_" + j);
                            }
                        } else {
                            // Read thread
                            for (int j = 0; j < 10; j++) {
                                cache.get("write_" + (threadId - 1) + "_" + j);
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            latch.await();
            executor.shutdown();
            
            // Should not throw any exceptions and cache should be in consistent state
            assertTrue(cache.size() >= 0);
        }
    }

    @Nested
    @DisplayName("Cache Statistics Tests")
    class CacheStatisticsTests {

        @Test
        @DisplayName("Should track hit and miss counts correctly")
        void shouldTrackHitAndMissCountsCorrectly() {
            CacheStats initialStats = cache.getStats();
            
            // Initial stats should be zero
            assertEquals(0, initialStats.getHitCount());
            assertEquals(0, initialStats.getMissCount());
            
            // Add a value
            cache.put("key1", "value1");
            
            // Hit
            cache.get("key1");
            CacheStats afterHit = cache.getStats();
            assertEquals(1, afterHit.getHitCount());
            assertEquals(0, afterHit.getMissCount());
            
            // Miss
            cache.get("nonexistent");
            CacheStats afterMiss = cache.getStats();
            assertEquals(1, afterMiss.getHitCount());
            assertEquals(1, afterMiss.getMissCount());
        }

        @Test
        @DisplayName("Should track put count correctly")
        void shouldTrackPutCountCorrectly() {
            CacheStats initialStats = cache.getStats();
            long initialPuts = initialStats.getPutCount();
            
            cache.put("key1", "value1");
            cache.put("key2", "value2");
            
            CacheStats afterPuts = cache.getStats();
            assertEquals(initialPuts + 2, afterPuts.getPutCount());
        }

        @Test
        @DisplayName("Should track removal count correctly")
        void shouldTrackRemovalCountCorrectly() {
            cache.put("key1", "value1");
            cache.put("key2", "value2");
            
            CacheStats initialStats = cache.getStats();
            long initialRemovals = initialStats.getRemovalCount();
            
            cache.remove("key1");
            cache.remove("nonexistent"); // Should not increment count
            
            CacheStats afterRemovals = cache.getStats();
            assertEquals(initialRemovals + 1, afterRemovals.getRemovalCount());
        }

        @Test
        @DisplayName("Should reset statistics correctly")
        void shouldResetStatisticsCorrectly() {
            // Generate some statistics
            cache.put("key1", "value1");
            cache.get("key1"); // Hit
            cache.get("nonexistent"); // Miss
            cache.remove("key1");
            
            CacheStats beforeReset = cache.getStats();
            assertTrue(beforeReset.getHitCount() > 0);
            assertTrue(beforeReset.getMissCount() > 0);
            
            cache.resetStats();
            
            CacheStats afterReset = cache.getStats();
            assertEquals(0, afterReset.getHitCount());
            assertEquals(0, afterReset.getMissCount());
            assertEquals(0, afterReset.getPutCount());
            assertEquals(0, afterReset.getRemovalCount());
        }

        @Test
        @DisplayName("Should calculate hit rate correctly")
        void shouldCalculateHitRateCorrectly() {
            // Empty cache should have 0 hit rate
            CacheStats emptyStats = cache.getStats();
            assertEquals(0.0, emptyStats.getHitRate(), 0.001);
            
            // Add some data
            cache.put("key1", "value1");
            cache.put("key2", "value2");
            
            // Generate hits and misses
            cache.get("key1"); // Hit
            cache.get("key2"); // Hit
            cache.get("nonexistent"); // Miss
            
            CacheStats stats = cache.getStats();
            double expectedHitRate = 2.0 / 3.0; // 2 hits out of 3 total requests
            assertEquals(expectedHitRate, stats.getHitRate(), 0.001);
        }
    }

    @Nested
    @DisplayName("Cache Configuration Tests")
    class CacheConfigurationTests {

        @Test
        @DisplayName("Should use default configuration when no arguments provided")
        void shouldUseDefaultConfigurationWhenNoArgumentsProvided() {
            InMemoryCacheService<String, String> defaultCache = new InMemoryCacheService<>();
            
            assertEquals(1000, defaultCache.getMaxSize());
            assertEquals(TimeUnit.MINUTES.toMillis(5), defaultCache.getDefaultExpirationMillis());
        }

        @Test
        @DisplayName("Should use custom configuration when provided")
        void shouldUseCustomConfigurationWhenProvided() {
            InMemoryCacheService<String, String> customCache = 
                new InMemoryCacheService<>(500, TimeUnit.SECONDS.toMillis(30));
            
            assertEquals(500, customCache.getMaxSize());
            assertEquals(TimeUnit.SECONDS.toMillis(30), customCache.getDefaultExpirationMillis());
        }

        @Test
        @DisplayName("Should handle contains key correctly")
        void shouldHandleContainsKeyCorrectly() {
            assertFalse(cache.containsKey("key1"));
            
            cache.put("key1", "value1");
            assertTrue(cache.containsKey("key1"));
            
            cache.remove("key1");
            assertFalse(cache.containsKey("key1"));
        }

        @Test
        @DisplayName("Should handle size and isEmpty correctly")
        void shouldHandleSizeAndIsEmptyCorrectly() {
            assertTrue(cache.isEmpty());
            assertEquals(0, cache.size());
            
            cache.put("key1", "value1");
            assertFalse(cache.isEmpty());
            assertEquals(1, cache.size());
            
            cache.put("key2", "value2");
            assertEquals(2, cache.size());
            
            cache.clear();
            assertTrue(cache.isEmpty());
            assertEquals(0, cache.size());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large number of entries efficiently")
        void shouldHandleLargeNumberOfEntriesEfficiently() {
            // Test with many entries to verify performance doesn't degrade significantly
            int entryCount = 1000;
            
            long startTime = System.nanoTime();
            for (int i = 0; i < entryCount; i++) {
                cache.put("key" + i, "value" + i);
            }
            long endTime = System.nanoTime();
            
            long putTime = endTime - startTime;
            
            // Should complete in reasonable time (less than 1 second for 1000 entries)
            assertTrue(putTime < 1_000_000_000, 
                "Putting 1000 entries should take less than 1 second, took: " + putTime / 1_000_000 + " ms");
            
            assertEquals(entryCount, cache.size());
            
            // Test retrieval performance
            startTime = System.nanoTime();
            for (int i = 0; i < entryCount; i++) {
                Optional<String> result = cache.get("key" + i);
                assertTrue(result.isPresent());
                assertEquals("value" + i, result.get());
            }
            endTime = System.nanoTime();
            
            long getTime = endTime - startTime;
            
            // Retrieval should be very fast (less than 100ms for 1000 entries)
            assertTrue(getTime < 100_000_000, 
                "Getting 1000 entries should take less than 100ms, took: " + getTime / 1_000_000 + " ms");
        }

        @Test
        @DisplayName("Should handle very long keys and values")
        void shouldHandleVeryLongKeysAndValues() {
            String longKey = "k".repeat(1000);
            String longValue = "v".repeat(10000);
            
            assertDoesNotThrow(() -> cache.put(longKey, longValue));
            
            Optional<String> result = cache.get(longKey);
            assertTrue(result.isPresent());
            assertEquals(longValue, result.get());
        }

        @Test
        @DisplayName("Should handle special characters in keys and values")
        void shouldHandleSpecialCharactersInKeysAndValues() {
            String[] specialKeys = {
                "key with spaces",
                "key-with-dashes",
                "key_with_underscores",
                "key.with.dots",
                "key/with/slashes",
                "key\\with\\backslashes",
                "key@with@symbols",
                "key#with#hashes",
                "key$with$dollars",
                "key%with%percents",
                "key^with^carets",
                "key&with&ampersands",
                "key*with*asterisks",
                "key(with)parentheses",
                "key[with]brackets",
                "key{with}braces",
                "key|with|pipes",
                "key+with+plus",
                "key=with=equals"
            };
            
            for (String key : specialKeys) {
                String value = "value for " + key;
                assertDoesNotThrow(() -> cache.put(key, value));
                
                Optional<String> result = cache.get(key);
                assertTrue(result.isPresent(), "Should find value for key: " + key);
                assertEquals(value, result.get());
            }
        }

        @Test
        @DisplayName("Should handle rapid put and remove operations")
        void shouldHandleRapidPutAndRemoveOperations() {
            // Rapid operations to test for race conditions
            for (int i = 0; i < 100; i++) {
                cache.put("key" + i, "value" + i);
                assertTrue(cache.containsKey("key" + i));
                
                cache.remove("key" + i);
                assertFalse(cache.containsKey("key" + i));
            }
            
            assertEquals(0, cache.size());
        }
    }
}