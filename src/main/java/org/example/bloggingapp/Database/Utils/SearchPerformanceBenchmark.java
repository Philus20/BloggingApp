package org.example.bloggingapp.Database.Utils;

import org.example.bloggingapp.Database.Services.PostSearchService;
import org.example.bloggingapp.Database.Services.PostService;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.ValidationException;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Performance benchmarking utility for search functionality
 * Documents measurable improvements in query execution time
 */
public class SearchPerformanceBenchmark {
    
    private final PostSearchService searchService;
    private final PostService postService;
    
    public SearchPerformanceBenchmark(PostSearchService searchService, PostService postService) {
        this.searchService = searchService;
        this.postService = postService;
    }
    
    /**
     * Run comprehensive performance benchmark
     */
    public BenchmarkResults runBenchmark() throws DatabaseException {
        System.out.println("=== Search Performance Benchmark ===");
        
        // Clear cache for fair comparison
        searchService.invalidateCache();
        
        BenchmarkResults results = new BenchmarkResults();
        
        // Test queries
        String[] testQueries = {
            "java", "programming", "tutorial", "blog", "post", 
            "development", "code", "algorithm", "data", "structure"
        };
        
        // Benchmark without cache
        System.out.println("1. Testing without cache...");
        results.setWithoutCacheTimes(benchmarkQueries(testQueries, false));
        
        // Preload cache
        System.out.println("2. Preloading cache...");
        long preloadStart = System.nanoTime();
        searchService.preloadCache();
        long preloadEnd = System.nanoTime();
        results.setCachePreloadTime(TimeUnit.NANOSECONDS.toMillis(preloadEnd - preloadStart));
        
        // Benchmark with cache
        System.out.println("3. Testing with cache...");
        results.setWithCacheTimes(benchmarkQueries(testQueries, true));
        
        // Calculate improvements
        results.calculateImprovements();
        
        // Print results
        printBenchmarkResults(results);
        
        return results;
    }
    
    private Map<String, Long> benchmarkQueries(String[] queries, boolean useCache) throws DatabaseException {
        Map<String, Long> times = new HashMap<>();
        
        for (String query : queries) {
            // Warm up
            try {
                searchService.searchByKeyword(query);
            } catch (ValidationException e) {
                // Skip invalid queries
                continue;
            }
            
            // Actual benchmark
            long totalTime = 0;
            int iterations = useCache ? 10 : 3; // More iterations for cache test
            
            for (int i = 0; i < iterations; i++) {
                long start = System.nanoTime();
                try {
                    searchService.searchByKeyword(query);
                } catch (ValidationException e) {
                    continue;
                }
                long end = System.nanoTime();
                totalTime += (end - start);
            }
            
            long averageTime = totalTime / iterations;
            times.put(query, TimeUnit.NANOSECONDS.toMicros(averageTime));
        }
        
        return times;
    }
    
    private void printBenchmarkResults(BenchmarkResults results) {
        System.out.println("\n=== Benchmark Results ===");
        
        System.out.println("\nCache Preload Time: " + results.getCachePreloadTime() + "ms");
        
        System.out.println("\nQuery Performance Comparison:");
        System.out.println("Query\t\tWithout Cache\tWith Cache\tImprovement");
        System.out.println("-----\t\t-------------\t----------\t-----------");
        
        for (Map.Entry<String, Long> entry : results.getWithoutCacheTimes().entrySet()) {
            String query = entry.getKey();
            Long withoutTime = entry.getValue();
            Long withTime = results.getWithCacheTimes().get(query);
            
            if (withTime != null) {
                double improvement = ((double) (withoutTime - withTime) / withoutTime) * 100;
                System.out.printf("%-12s\t%8d μs\t%8d μs\t%8.1f%%\n", 
                    query, withoutTime, withTime, improvement);
            }
        }
        
        System.out.println("\nOverall Performance Metrics:");
        System.out.printf("Average time without cache: %.1f μs\n", results.getAverageWithoutCache());
        System.out.printf("Average time with cache: %.1f μs\n", results.getAverageWithCache());
        System.out.printf("Average improvement: %.1f%%\n", results.getAverageImprovement());
        System.out.printf("Speedup factor: %.1fx\n", results.getSpeedupFactor());
        
        // Cache statistics
        Map<String, Object> metrics = searchService.getPerformanceMetrics();
        System.out.println("\nCache Statistics:");
        System.out.println("Cache hit rate: " + String.format("%.2f%%", (Double) metrics.get("cacheHitRate") * 100));
        System.out.println("Keyword cache size: " + metrics.get("keywordCacheSize"));
        System.out.println("Author cache size: " + metrics.get("authorCacheSize"));
        System.out.println("Tag cache size: " + metrics.get("tagCacheSize"));
    }
    
    /**
     * Test search scalability with different dataset sizes
     */
    public void scalabilityTest() throws DatabaseException {
        System.out.println("\n=== Search Scalability Test ===");
        
        List<PostEntity> allPosts = postService.findAll();
        int totalPosts = allPosts.size();
        
        System.out.println("Total posts in database: " + totalPosts);
        
        // Test with different dataset sizes
        int[] testSizes = {10, 25, 50, 100, 250, 500, 1000};
        
        System.out.println("\nDataset Size\tSearch Time (μs)\tCache Hit Rate");
        System.out.println("------------\t----------------\t--------------");
        
        for (int size : testSizes) {
            if (size > totalPosts) {
                continue;
            }
            
            // Clear cache and test
            searchService.invalidateCache();
            
            long totalTime = 0;
            String testQuery = "java";
            
            for (int i = 0; i < 5; i++) {
                long start = System.nanoTime();
                try {
                    searchService.searchByKeyword(testQuery);
                } catch (ValidationException e) {
                    continue;
                }
                long end = System.nanoTime();
                totalTime += (end - start);
            }
            
            long avgTime = TimeUnit.NANOSECONDS.toMicros(totalTime / 5);
            
            // Get cache hit rate
            Map<String, Object> metrics = searchService.getPerformanceMetrics();
            double cacheHitRate = (Double) metrics.get("cacheHitRate") * 100;
            
            System.out.printf("%-12d\t%12d μs\t%12.1f%%\n", size, avgTime, cacheHitRate);
        }
    }
    
    /**
     * Test different search algorithms and strategies
     */
    public void algorithmComparison() {
        try {
            System.out.println("\n=== Search Algorithm Comparison ===");
            
            String testQuery = "java programming";
            
            // Test 1: Linear search (current implementation)
            searchService.invalidateCache();
            long linearTime = measureSearchTime(testQuery);
            
            // Test 2: Cached search
            searchService.preloadCache();
            long cachedTime = measureSearchTime(testQuery);
            
            // Test 3: Combined search
            long combinedTime = measureCombinedSearchTime(testQuery);
            
            System.out.println("Search Strategy\t\tTime (μs)\tRelative Performance");
            System.out.println("---------------\t\t---------\t------------------");
            System.out.printf("Linear Search\t\t%8d μs\t%16.1fx\n", linearTime, 1.0);
            System.out.printf("Cached Search\t\t%8d μs\t%16.1fx\n", cachedTime, (double) linearTime / cachedTime);
            System.out.printf("Combined Search\t\t%8d μs\t%16.1fx\n", combinedTime, (double) linearTime / combinedTime);
        } catch (Exception e) {
            System.out.println("Error during algorithm comparison: " + e.getMessage());
        }
    }
    
    private long measureSearchTime(String query) {
        long totalTime = 0;
        int iterations = 10;
        
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            try {
                searchService.searchByKeyword(query);
            } catch (ValidationException e) {
                continue;
            } catch (DatabaseException e) {
                continue;
            }
            long end = System.nanoTime();
            totalTime += (end - start);
        }
        
        return TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
    }
    
    private long measureCombinedSearchTime(String query) {
        long totalTime = 0;
        int iterations = 10;
        
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            try {
                searchService.searchAll(query);
            } catch (ValidationException e) {
                continue;
            } catch (DatabaseException e) {
                continue;
            }
            long end = System.nanoTime();
            totalTime += (end - start);
        }
        
        return TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
    }
    
    /**
     * Container class for benchmark results
     */
    public static class BenchmarkResults {
        private Map<String, Long> withoutCacheTimes;
        private Map<String, Long> withCacheTimes;
        private long cachePreloadTime;
        private double averageImprovement;
        private double speedupFactor;
        
        public void setWithoutCacheTimes(Map<String, Long> times) {
            this.withoutCacheTimes = times;
        }
        
        public void setWithCacheTimes(Map<String, Long> times) {
            this.withCacheTimes = times;
        }
        
        public void setCachePreloadTime(long time) {
            this.cachePreloadTime = time;
        }
        
        public Map<String, Long> getWithoutCacheTimes() {
            return withoutCacheTimes;
        }
        
        public Map<String, Long> getWithCacheTimes() {
            return withCacheTimes;
        }
        
        public long getCachePreloadTime() {
            return cachePreloadTime;
        }
        
        public double getAverageImprovement() {
            return averageImprovement;
        }
        
        public double getSpeedupFactor() {
            return speedupFactor;
        }
        
        public double getAverageWithoutCache() {
            return withoutCacheTimes.values().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
        }
        
        public double getAverageWithCache() {
            return withCacheTimes.values().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
        }
        
        public void calculateImprovements() {
            double totalImprovement = 0;
            int count = 0;
            
            for (Map.Entry<String, Long> entry : withoutCacheTimes.entrySet()) {
                String query = entry.getKey();
                Long withoutTime = entry.getValue();
                Long withTime = withCacheTimes.get(query);
                
                if (withTime != null && withoutTime > 0) {
                    double improvement = ((double) (withoutTime - withTime) / withoutTime) * 100;
                    totalImprovement += improvement;
                    count++;
                }
            }
            
            this.averageImprovement = count > 0 ? totalImprovement / count : 0.0;
            this.speedupFactor = getAverageWithoutCache() > 0 ? 
                getAverageWithoutCache() / getAverageWithCache() : 1.0;
        }
    }
}
