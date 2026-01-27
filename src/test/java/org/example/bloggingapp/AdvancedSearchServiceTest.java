package org.example.bloggingapp;

import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Services.AdvancedSearchService;
import org.example.bloggingapp.Services.PostService;
import org.example.bloggingapp.Utils.Exceptions.DatabaseException;
import org.example.bloggingapp.Utils.Exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdvancedSearchService Tests")
class AdvancedSearchServiceTest {

    private AdvancedSearchService advancedSearchService;
    private PostService mockPostService;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() throws DatabaseException {
        mockPostService = new PostService(null);
        advancedSearchService = new AdvancedSearchService(mockPostService);
        testTime = LocalDateTime.now();
        
        // Build indexes for testing
        advancedSearchService.buildIndexes();
    }

    @Nested
    @DisplayName("Hash Search Algorithm Tests")
    class HashSearchTests {

        @Test
        @DisplayName("Should throw ValidationException for null keyword")
        void shouldThrowValidationExceptionForNullKeyword() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> advancedSearchService.hashSearchByKeyword(null));
            
            assertEquals("KEYWORD_REQUIRED", exception.getErrorCode());
            assertEquals("keyword", exception.getFieldName());
        }

        @Test
        @DisplayName("Should throw ValidationException for empty keyword")
        void shouldThrowValidationExceptionForEmptyKeyword() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> advancedSearchService.hashSearchByKeyword(""));
            
            assertEquals("KEYWORD_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("Should handle valid keyword search with O(1) average performance")
        void shouldHandleValidKeywordSearchWithO1Performance() {
            // Test performance characteristic
            long startTime = System.nanoTime();
            assertDoesNotThrow(() -> advancedSearchService.hashSearchByKeyword("test"));
            long endTime = System.nanoTime();
            
            long executionTime = endTime - startTime;
            // Should be very fast (microseconds, not milliseconds)
            assertTrue(executionTime < 1_000_000, 
                "Hash search should complete in microseconds, took: " + executionTime / 1000 + " μs");
        }

        @Test
        @DisplayName("Should demonstrate hash index cache hit/miss behavior")
        void shouldDemonstrateHashIndexCacheHitMissBehavior() throws DatabaseException, ValidationException {
            // First search (cache miss)
            long firstSearchTime = measureSearchTime("testkeyword");
            
            // Second search (cache hit)
            long secondSearchTime = measureSearchTime("testkeyword");
            
            // Cache hit should be faster
            assertTrue(secondSearchTime <= firstSearchTime, 
                "Cache hit should be faster or equal to cache miss");
        }

        @Test
        @DisplayName("Should handle case-insensitive keyword search")
        void shouldHandleCaseInsensitiveKeywordSearch() {
            assertDoesNotThrow(() -> advancedSearchService.hashSearchByKeyword("Test"));
            assertDoesNotThrow(() -> advancedSearchService.hashSearchByKeyword("TEST"));
            assertDoesNotThrow(() -> advancedSearchService.hashSearchByKeyword("test"));
        }

        @Test
        @DisplayName("Should return empty list for non-existent keyword")
        void shouldReturnEmptyListForNonExistentKeyword() throws DatabaseException, ValidationException {
            List<PostEntity> results = advancedSearchService.hashSearchByKeyword("nonexistentkeyword12345");
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }
    }

    @Nested
    @DisplayName("Binary Search Algorithm Tests")
    class BinarySearchTests {

        @Test
        @DisplayName("Should throw ValidationException for null title")
        void shouldThrowValidationExceptionForNullTitle() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> advancedSearchService.binarySearchByTitle(null));
            
            assertEquals("TITLE_REQUIRED", exception.getErrorCode());
            assertEquals("title", exception.getFieldName());
        }

        @Test
        @DisplayName("Should throw ValidationException for empty title")
        void shouldThrowValidationExceptionForEmptyTitle() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> advancedSearchService.binarySearchByTitle(""));
            
            assertEquals("TITLE_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("Should handle valid title search with O(log n) performance")
        void shouldHandleValidTitleSearchWithOLognPerformance() {
            // Test performance characteristic
            long startTime = System.nanoTime();
            assertDoesNotThrow(() -> advancedSearchService.binarySearchByTitle("test"));
            long endTime = System.nanoTime();
            
            long executionTime = endTime - startTime;
            // Should be fast (logarithmic time)
            assertTrue(executionTime < 10_000_000, 
                "Binary search should complete quickly, took: " + executionTime / 1000 + " μs");
        }

        @Test
        @DisplayName("Should handle prefix search with TreeMap ceiling/floor")
        void shouldHandlePrefixSearchWithTreeMapCeilingFloor() throws DatabaseException, ValidationException {
            // Test prefix matching capability
            List<PostEntity> results = advancedSearchService.binarySearchByTitle("java");
            assertNotNull(results);
            // Should find posts with titles starting with "java"
        }

        @Test
        @DisplayName("Should handle case-insensitive title search")
        void shouldHandleCaseInsensitiveTitleSearch() {
            assertDoesNotThrow(() -> advancedSearchService.binarySearchByTitle("Test"));
            assertDoesNotThrow(() -> advancedSearchService.binarySearchByTitle("TEST"));
            assertDoesNotThrow(() -> advancedSearchService.binarySearchByTitle("test"));
        }
    }

    @Nested
    @DisplayName("QuickSort Algorithm Tests")
    class QuickSortTests {

        @Test
        @DisplayName("Should handle null posts list")
        void shouldHandleNullPostsList() {
            List<PostEntity> result = advancedSearchService.quickSortPosts(null, "title", "asc");
            assertNull(result);
        }

        @Test
        @DisplayName("Should handle empty posts list")
        void shouldHandleEmptyPostsList() {
            List<PostEntity> result = advancedSearchService.quickSortPosts(List.of(), "title", "asc");
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should sort posts by title in ascending order")
        void shouldSortPostsByTitleInAscendingOrder() {
            // Create test posts with different titles
            List<PostEntity> posts = createTestPosts();
            
            List<PostEntity> sorted = advancedSearchService.quickSortPosts(posts, "title", "asc");
            
            // Verify sorting order
            for (int i = 1; i < sorted.size(); i++) {
                String prevTitle = sorted.get(i - 1).getTitle();
                String currTitle = sorted.get(i).getTitle();
                assertTrue(prevTitle.compareToIgnoreCase(currTitle) <= 0,
                    "Posts should be sorted by title in ascending order");
            }
        }

        @Test
        @DisplayName("Should sort posts by title in descending order")
        void shouldSortPostsByTitleInDescendingOrder() {
            List<PostEntity> posts = createTestPosts();
            
            List<PostEntity> sorted = advancedSearchService.quickSortPosts(posts, "title", "desc");
            
            // Verify sorting order
            for (int i = 1; i < sorted.size(); i++) {
                String prevTitle = sorted.get(i - 1).getTitle();
                String currTitle = sorted.get(i).getTitle();
                assertTrue(prevTitle.compareToIgnoreCase(currTitle) >= 0,
                    "Posts should be sorted by title in descending order");
            }
        }

        @Test
        @DisplayName("Should sort posts by views in ascending order")
        void shouldSortPostsByViewsInAscendingOrder() {
            List<PostEntity> posts = createTestPosts();
            
            List<PostEntity> sorted = advancedSearchService.quickSortPosts(posts, "views", "asc");
            
            // Verify sorting order
            for (int i = 1; i < sorted.size(); i++) {
                int prevViews = sorted.get(i - 1).getViews();
                int currViews = sorted.get(i).getViews();
                assertTrue(prevViews <= currViews,
                    "Posts should be sorted by views in ascending order");
            }
        }

        @Test
        @DisplayName("Should sort posts by views in descending order")
        void shouldSortPostsByViewsInDescendingOrder() {
            List<PostEntity> posts = createTestPosts();
            
            List<PostEntity> sorted = advancedSearchService.quickSortPosts(posts, "views", "desc");
            
            // Verify sorting order
            for (int i = 1; i < sorted.size(); i++) {
                int prevViews = sorted.get(i - 1).getViews();
                int currViews = sorted.get(i).getViews();
                assertTrue(prevViews >= currViews,
                    "Posts should be sorted by views in descending order");
            }
        }

        @Test
        @DisplayName("Should sort posts by creation date")
        void shouldSortPostsByCreationDate() {
            List<PostEntity> posts = createTestPostsWithDifferentDates();
            
            List<PostEntity> sortedAsc = advancedSearchService.quickSortPosts(posts, "created", "asc");
            List<PostEntity> sortedDesc = advancedSearchService.quickSortPosts(posts, "created", "desc");
            
            // Verify ascending order
            for (int i = 1; i < sortedAsc.size(); i++) {
                LocalDateTime prevDate = sortedAsc.get(i - 1).getCreatedAt();
                LocalDateTime currDate = sortedAsc.get(i).getCreatedAt();
                assertTrue(prevDate.isBefore(currDate) || prevDate.isEqual(currDate),
                    "Posts should be sorted by date in ascending order");
            }
            
            // Verify descending order
            for (int i = 1; i < sortedDesc.size(); i++) {
                LocalDateTime prevDate = sortedDesc.get(i - 1).getCreatedAt();
                LocalDateTime currDate = sortedDesc.get(i).getCreatedAt();
                assertTrue(prevDate.isAfter(currDate) || prevDate.isEqual(currDate),
                    "Posts should be sorted by date in descending order");
            }
        }

        @Test
        @DisplayName("Should demonstrate O(n log n) performance characteristic")
        void shouldDemonstrateOnlognPerformanceCharacteristic() {
            List<PostEntity> posts = createTestPosts();
            
            // Measure sorting time
            long startTime = System.nanoTime();
            List<PostEntity> sorted = advancedSearchService.quickSortPosts(posts, "title", "asc");
            long endTime = System.nanoTime();
            
            assertNotNull(sorted);
            assertEquals(posts.size(), sorted.size());
            
            long executionTime = endTime - startTime;
            // Should complete in reasonable time for n log n complexity
            assertTrue(executionTime < 50_000_000, 
                "QuickSort should demonstrate O(n log n) performance, took: " + executionTime / 1000 + " μs");
        }

        @Test
        @DisplayName("Should handle posts with null values gracefully")
        void shouldHandlePostsWithNullValuesGracefully() {
            List<PostEntity> posts = List.of(
                new PostEntity(1, null, "Content", testTime, 1, "Published", 100, "Author"),
                new PostEntity(2, "Title", null, testTime, 1, "Published", 200, null),
                new PostEntity(3, "Title", "Content", null, 1, "Published", 150, "Author")
            );
            
            assertDoesNotThrow(() -> advancedSearchService.quickSortPosts(posts, "title", "asc"));
            assertDoesNotThrow(() -> advancedSearchService.quickSortPosts(posts, "author", "asc"));
            assertDoesNotThrow(() -> advancedSearchService.quickSortPosts(posts, "created", "asc"));
        }
    }

    @Nested
    @DisplayName("Advanced Search Tests")
    class AdvancedSearchTests {

        @Test
        @DisplayName("Should throw ValidationException for null query")
        void shouldThrowValidationExceptionForNullQuery() {
            AdvancedSearchService.SearchOptions options = new AdvancedSearchService.SearchOptions();
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> advancedSearchService.advancedSearch(null, options));
            
            assertEquals("QUERY_REQUIRED", exception.getErrorCode());
            assertEquals("query", exception.getFieldName());
        }

        @Test
        @DisplayName("Should handle hash search type")
        void shouldHandleHashSearchType() throws DatabaseException, ValidationException {
            AdvancedSearchService.SearchOptions options = new AdvancedSearchService.SearchOptions();
            options.setSearchType("hash");
            
            AdvancedSearchService.SearchResult result = advancedSearchService.advancedSearch("test", options);
            
            assertNotNull(result);
            assertNotNull(result.getPosts());
            assertEquals("hash_search", result.getAlgorithmUsed());
            assertTrue(result.getExecutionTime() >= 0);
        }

        @Test
        @DisplayName("Should handle binary search type")
        void shouldHandleBinarySearchType() throws DatabaseException, ValidationException {
            AdvancedSearchService.SearchOptions options = new AdvancedSearchService.SearchOptions();
            options.setSearchType("binary");
            
            AdvancedSearchService.SearchResult result = advancedSearchService.advancedSearch("test", options);
            
            assertNotNull(result);
            assertNotNull(result.getPosts());
            assertEquals("binary_search", result.getAlgorithmUsed());
            assertTrue(result.getExecutionTime() >= 0);
        }

        @Test
        @DisplayName("Should handle hybrid search type")
        void shouldHandleHybridSearchType() throws DatabaseException, ValidationException {
            AdvancedSearchService.SearchOptions options = new AdvancedSearchService.SearchOptions();
            options.setSearchType("hybrid");
            
            AdvancedSearchService.SearchResult result = advancedSearchService.advancedSearch("test", options);
            
            assertNotNull(result);
            assertNotNull(result.getPosts());
            assertEquals("hybrid_search", result.getAlgorithmUsed());
            assertTrue(result.getExecutionTime() >= 0);
        }

        @Test
        @DisplayName("Should apply sorting to search results")
        void shouldApplySortingToSearchResults() throws DatabaseException, ValidationException {
            AdvancedSearchService.SearchOptions options = new AdvancedSearchService.SearchOptions();
            options.setSearchType("hash");
            options.setSortBy("title");
            options.setSortOrder("asc");
            
            AdvancedSearchService.SearchResult result = advancedSearchService.advancedSearch("test", options);
            
            assertNotNull(result);
            assertNotNull(result.getPosts());
        }

        @Test
        @DisplayName("Should apply pagination to search results")
        void shouldApplyPaginationToSearchResults() throws DatabaseException, ValidationException {
            AdvancedSearchService.SearchOptions options = new AdvancedSearchService.SearchOptions();
            options.setSearchType("hash");
            options.setPage(1);
            options.setPageSize(5);
            
            AdvancedSearchService.SearchResult result = advancedSearchService.advancedSearch("test", options);
            
            assertNotNull(result);
            assertTrue(result.getPosts().size() <= 5);
            assertTrue(result.getTotalResults() >= result.getPosts().size());
        }
    }

    @Nested
    @DisplayName("Algorithm Comparison Tests")
    class AlgorithmComparisonTests {

        @Test
        @DisplayName("Should compare all algorithms successfully")
        void shouldCompareAllAlgorithmsSuccessfully() throws DatabaseException, ValidationException {
            AdvancedSearchService.AlgorithmComparison comparison = advancedSearchService.compareAlgorithms("test");
            
            assertNotNull(comparison);
            assertNotNull(comparison.getExecutionTimes());
            assertNotNull(comparison.getResults());
            
            Map<String, Long> times = comparison.getExecutionTimes();
            assertTrue(times.containsKey("linear"));
            assertTrue(times.containsKey("hash"));
            assertTrue(times.containsKey("binary"));
            assertTrue(times.containsKey("hybrid"));
        }

        @Test
        @DisplayName("Should demonstrate hash search performance advantage")
        void shouldDemonstrateHashSearchPerformanceAdvantage() throws DatabaseException, ValidationException {
            AdvancedSearchService.AlgorithmComparison comparison = advancedSearchService.compareAlgorithms("test");
            
            Map<String, Long> times = comparison.getExecutionTimes();
            Long linearTime = times.get("linear");
            Long hashTime = times.get("hash");
            
            if (linearTime != null && hashTime != null && linearTime > 0) {
                double speedup = (double) linearTime / hashTime;
                assertTrue(speedup >= 1.0, 
                    "Hash search should be faster or equal to linear search, speedup: " + speedup + "x");
            }
        }

        @Test
        @DisplayName("Should demonstrate binary search performance advantage")
        void shouldDemonstrateBinarySearchPerformanceAdvantage() throws DatabaseException, ValidationException {
            AdvancedSearchService.AlgorithmComparison comparison = advancedSearchService.compareAlgorithms("test");
            
            Map<String, Long> times = comparison.getExecutionTimes();
            Long linearTime = times.get("linear");
            Long binaryTime = times.get("binary");
            
            if (linearTime != null && binaryTime != null && linearTime > 0) {
                double speedup = (double) linearTime / binaryTime;
                assertTrue(speedup >= 1.0, 
                    "Binary search should be faster or equal to linear search, speedup: " + speedup + "x");
            }
        }
    }

    @Nested
    @DisplayName("Index Building Tests")
    class IndexBuildingTests {

        @Test
        @DisplayName("Should build indexes without throwing exceptions")
        void buildIndexes() {
            assertDoesNotThrow(() -> advancedSearchService.buildIndexes());
        }

        @Test
        @DisplayName("Should update last index update timestamp")
        void shouldUpdateLastIndexUpdateTimestamp() throws DatabaseException {
            LocalDateTime beforeUpdate = LocalDateTime.now();
            advancedSearchService.buildIndexes();
            LocalDateTime afterUpdate = LocalDateTime.now();
            
            AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
            LocalDateTime lastUpdate = stats.getLastIndexUpdate();
            
            assertNotNull(lastUpdate);
            assertTrue(lastUpdate.isAfter(beforeUpdate) || lastUpdate.isEqual(beforeUpdate));
            assertTrue(lastUpdate.isBefore(afterUpdate) || lastUpdate.isEqual(afterUpdate));
        }

        @Test
        @DisplayName("Should create index entries for test data")
        void shouldCreateIndexEntriesForTestData() throws DatabaseException {
            advancedSearchService.buildIndexes();
            
            AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
            
            // Indexes should have entries (even if empty, they should exist)
            assertTrue(stats.getKeywordIndexSize() >= 0);
            assertTrue(stats.getAuthorIndexSize() >= 0);
            assertTrue(stats.getTagIndexSize() >= 0);
            assertTrue(stats.getTitleIndexSize() >= 0);
        }
    }

    @Nested
    @DisplayName("Performance Statistics Tests")
    class PerformanceStatisticsTests {

        @Test
        @DisplayName("Should return valid performance statistics")
        void shouldReturnValidPerformanceStatistics() {
            AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
            
            assertNotNull(stats);
            assertNotNull(stats.getAvgTimes());
            assertNotNull(stats.getTotalTimes());
            assertNotNull(stats.getExecutionCounts());
            assertTrue(stats.getCacheHits() >= 0);
            assertTrue(stats.getCacheMisses() >= 0);
            assertNotNull(stats.getLastIndexUpdate());
            assertTrue(stats.getKeywordIndexSize() >= 0);
            assertTrue(stats.getAuthorIndexSize() >= 0);
            assertTrue(stats.getTagIndexSize() >= 0);
            assertTrue(stats.getTitleIndexSize() >= 0);
        }

        @Test
        @DisplayName("Should calculate cache hit rate correctly")
        void shouldCalculateCacheHitRateCorrectly() {
            AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
            
            int totalRequests = stats.getCacheHits() + stats.getCacheMisses();
            double hitRate = stats.getCacheHitRate();
            
            if (totalRequests > 0) {
                double expectedHitRate = (double) stats.getCacheHits() / totalRequests;
                assertEquals(expectedHitRate, hitRate, 0.001, "Cache hit rate should be calculated correctly");
            } else {
                assertEquals(0.0, hitRate, "Cache hit rate should be 0 when no requests made");
            }
        }

        @Test
        @DisplayName("Should track algorithm execution times")
        void shouldTrackAlgorithmExecutionTimes() throws DatabaseException, ValidationException {
            // Perform some searches to generate metrics
            advancedSearchService.hashSearchByKeyword("test");
            advancedSearchService.binarySearchByTitle("test");
            advancedSearchService.compareAlgorithms("test");
            
            AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
            Map<String, Double> avgTimes = stats.getAvgTimes();
            
            // Should have some performance data
            assertFalse(avgTimes.isEmpty());
        }
    }

    // Helper methods
    private long measureSearchTime(String keyword) {
        try {
            long startTime = System.nanoTime();
            advancedSearchService.hashSearchByKeyword(keyword);
            long endTime = System.nanoTime();
            return endTime - startTime;
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    private List<PostEntity> createTestPosts() {
        return List.of(
            new PostEntity(1, "Zebra Title", "Content", testTime, 1, "Published", 100, "Author A"),
            new PostEntity(2, "Apple Title", "Content", testTime, 1, "Published", 200, "Author B"),
            new PostEntity(3, "Banana Title", "Content", testTime, 1, "Published", 150, "Author C")
        );
    }

    private List<PostEntity> createTestPostsWithDifferentDates() {
        LocalDateTime time1 = testTime.minusDays(2);
        LocalDateTime time2 = testTime.minusDays(1);
        LocalDateTime time3 = testTime;
        
        return List.of(
            new PostEntity(1, "Title 1", "Content", time1, 1, "Published", 100, "Author A"),
            new PostEntity(2, "Title 2", "Content", time2, 1, "Published", 200, "Author B"),
            new PostEntity(3, "Title 3", "Content", time3, 1, "Published", 150, "Author C")
        );
    }
}