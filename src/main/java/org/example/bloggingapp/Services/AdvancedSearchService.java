package org.example.bloggingapp.Services;

import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Advanced Search Service with explicit data structures and algorithms
 * Implements QuickSort, Binary Search, and advanced indexing concepts
 */
public class AdvancedSearchService {
    
    private final PostService postService;
    
    // Advanced caching with multiple indexing strategies
    private final Map<String, List<PostEntity>> keywordIndex;
    private final Map<String, List<PostEntity>> authorIndex;
    private final Map<String, List<PostEntity>> tagIndex;
    private final TreeMap<String, List<PostEntity>> titleIndex; // Sorted for binary search
    private final Map<Integer, PostEntity> postByIdIndex;
    
    // Performance metrics
    private final Map<String, List<Long>> algorithmPerformanceMetrics;
    
    // Cache statistics
    private volatile int cacheHits = 0;
    private volatile int cacheMisses = 0;
    private volatile LocalDateTime lastIndexUpdate;
    
    public AdvancedSearchService(PostService postService) {
        this.postService = postService;
        this.keywordIndex = new ConcurrentHashMap<>();
        this.authorIndex = new ConcurrentHashMap<>();
        this.tagIndex = new ConcurrentHashMap<>();
        this.titleIndex = new TreeMap<>();
        this.postByIdIndex = new ConcurrentHashMap<>();
        this.algorithmPerformanceMetrics = new ConcurrentHashMap<>();
        this.lastIndexUpdate = LocalDateTime.now();
    }
    
    /**
     * Build comprehensive indexes for all posts
     * Demonstrates indexing concept similar to database indexes
     */
    public void buildIndexes() throws DatabaseException {
        long startTime = System.nanoTime();
        
        try {
            List<PostEntity> allPosts = postService.findAll();
            
            // Clear existing indexes
            clearIndexes();
            
            // Build keyword index (inverted index concept)
            for (PostEntity post : allPosts) {
                indexPostByKeywords(post);
                indexPostByAuthor(post);
                indexPostByTags(post);
                indexPostByTitle(post);
                indexPostById(post);
            }
            
            long endTime = System.nanoTime();
            recordPerformanceMetric("index_building", endTime - startTime);
            
            lastIndexUpdate = LocalDateTime.now();
            System.out.println("Indexes built successfully in " + (endTime - startTime) / 1_000_000 + "ms");
            
        } catch (Exception e) {
            throw new DatabaseException("INDEX_BUILD_ERROR", "Failed to build search indexes", e);
        }
    }
    
    /**
     * Binary Search implementation for title-based searches
     * O(log n) complexity for sorted data
     */
    public List<PostEntity> binarySearchByTitle(String title) throws ValidationException, DatabaseException {
        long startTime = System.nanoTime();
        
        try {
            if (title == null || title.trim().isEmpty()) {
                throw new ValidationException("TITLE_REQUIRED", "title", "Title cannot be null or empty");
            }
            
            String searchTitle = title.toLowerCase().trim();
            
            // Ensure indexes are built
            if (titleIndex.isEmpty()) {
                buildIndexes();
            }
            
            // Binary search in TreeMap
            List<PostEntity> results = new ArrayList<>();
            
            // Find the closest match in the sorted index
            Map.Entry<String, List<PostEntity>> entry = titleIndex.ceilingEntry(searchTitle);
            if (entry != null && entry.getKey().startsWith(searchTitle)) {
                results.addAll(entry.getValue());
            }
            
            // Also check for exact matches in nearby entries
            Map.Entry<String, List<PostEntity>> floorEntry = titleIndex.floorEntry(searchTitle);
            if (floorEntry != null && floorEntry.getKey().startsWith(searchTitle)) {
                results.addAll(floorEntry.getValue());
            }
            
            // Remove duplicates
            results = new ArrayList<>(new HashSet<>(results));
            
            long endTime = System.nanoTime();
            recordPerformanceMetric("binary_search", endTime - startTime);
            
            return results;
            
        } catch (Exception e) {
            throw new DatabaseException("BINARY_SEARCH_ERROR", "Failed to perform binary search", e);
        }
    }
    
    /**
     * QuickSort implementation for sorting posts
     * O(n log n) average case complexity
     */
    public List<PostEntity> quickSortPosts(List<PostEntity> posts, String sortBy, String order) {
        long startTime = System.nanoTime();
        
        try {
            if (posts == null || posts.size() <= 1) {
                return posts;
            }
            
            List<PostEntity> postsCopy = new ArrayList<>(posts);
            
            // Choose comparator based on sort criteria
            Comparator<PostEntity> comparator = getComparator(sortBy);
            
            // Apply QuickSort
            quickSort(postsCopy, 0, postsCopy.size() - 1, comparator);
            
            // Reverse if descending order
            if ("desc".equalsIgnoreCase(order)) {
                Collections.reverse(postsCopy);
            }
            
            long endTime = System.nanoTime();
            recordPerformanceMetric("quicksort", endTime - startTime);
            
            return postsCopy;
            
        } catch (Exception e) {
            System.err.println("Error during QuickSort: " + e.getMessage());
            return posts; // Fallback to original list
        }
    }
    
    /**
     * QuickSort algorithm implementation
     */
    private void quickSort(List<PostEntity> posts, int low, int high, Comparator<PostEntity> comparator) {
        if (low < high) {
            int pivotIndex = partition(posts, low, high, comparator);
            quickSort(posts, low, pivotIndex - 1, comparator);
            quickSort(posts, pivotIndex + 1, high, comparator);
        }
    }
    
    private int partition(List<PostEntity> posts, int low, int high, Comparator<PostEntity> comparator) {
        PostEntity pivot = posts.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (comparator.compare(posts.get(j), pivot) <= 0) {
                i++;
                swap(posts, i, j);
            }
        }
        
        swap(posts, i + 1, high);
        return i + 1;
    }
    
    private void swap(List<PostEntity> posts, int i, int j) {
        PostEntity temp = posts.get(i);
        posts.set(i, posts.get(j));
        posts.set(j, temp);
    }
    
    /**
     * Hash-based search using keyword index
     * O(1) average case complexity for hash lookup
     */
    public List<PostEntity> hashSearchByKeyword(String keyword) throws ValidationException, DatabaseException {
        long startTime = System.nanoTime();
        
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new ValidationException("KEYWORD_REQUIRED", "keyword", "Keyword cannot be null or empty");
            }
            
            String normalizedKeyword = keyword.toLowerCase().trim();
            
            // Ensure indexes are built
            if (keywordIndex.isEmpty()) {
                buildIndexes();
            }
            
            // Hash-based lookup
            List<PostEntity> results = keywordIndex.get(normalizedKeyword);
            
            if (results != null) {
                cacheHits++;
                long endTime = System.nanoTime();
                recordPerformanceMetric("hash_search_hit", endTime - startTime);
                return new ArrayList<>(results);
            } else {
                cacheMisses++;
                long endTime = System.nanoTime();
                recordPerformanceMetric("hash_search_miss", endTime - startTime);
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            throw new DatabaseException("HASH_SEARCH_ERROR", "Failed to perform hash search", e);
        }
    }
    
    /**
     * Advanced search combining multiple algorithms
     */
    public SearchResult advancedSearch(String query, SearchOptions options) throws ValidationException, DatabaseException {
        long startTime = System.nanoTime();
        
        try {
            if (query == null || query.trim().isEmpty()) {
                throw new ValidationException("QUERY_REQUIRED", "query", "Search query cannot be null or empty");
            }
            
            List<PostEntity> results = new ArrayList<>();
            String algorithmUsed = "";
            
            // Choose algorithm based on search type
            switch (options.getSearchType().toLowerCase()) {
                case "binary":
                    results = binarySearchByTitle(query);
                    algorithmUsed = "binary_search";
                    break;
                case "hash":
                    results = hashSearchByKeyword(query);
                    algorithmUsed = "hash_search";
                    break;
                case "linear":
                    results = linearSearch(query);
                    algorithmUsed = "linear_search";
                    break;
                case "hybrid":
                    results = hybridSearch(query);
                    algorithmUsed = "hybrid_search";
                    break;
                default:
                    results = hashSearchByKeyword(query);
                    algorithmUsed = "hash_search";
            }
            
            // Apply sorting if requested
            if (options.getSortBy() != null && !options.getSortBy().isEmpty()) {
                results = quickSortPosts(results, options.getSortBy(), options.getSortOrder());
            }
            
            // Apply pagination
            int start = (options.getPage() - 1) * options.getPageSize();
            int end = Math.min(start + options.getPageSize(), results.size());
            List<PostEntity> paginatedResults = results.subList(start, end);
            
            long endTime = System.nanoTime();
            
            return new SearchResult(
                paginatedResults,
                results.size(),
                algorithmUsed,
                endTime - startTime,
                options
            );
            
        } catch (Exception e) {
            throw new DatabaseException("ADVANCED_SEARCH_ERROR", "Failed to perform advanced search", e);
        }
    }
    
    /**
     * Hybrid search combining multiple strategies
     */
    private List<PostEntity> hybridSearch(String query) throws DatabaseException, ValidationException {
        List<PostEntity> allResults = new ArrayList<>();
        
        // Try hash search first (fastest)
        allResults.addAll(hashSearchByKeyword(query));
        
        // Try binary search on titles
        allResults.addAll(binarySearchByTitle(query));
        
        // Remove duplicates and sort by relevance
        return new ArrayList<>(new HashSet<>(allResults));
    }
    
    /**
     * Linear search fallback (O(n) complexity)
     */
    private List<PostEntity> linearSearch(String query) throws DatabaseException {
        long startTime = System.nanoTime();
        
        try {
            List<PostEntity> allPosts = postService.findAll();
            String normalizedQuery = query.toLowerCase().trim();
            
            List<PostEntity> results = allPosts.stream()
                    .filter(post -> 
                        (post.getTitle() != null && post.getTitle().toLowerCase().contains(normalizedQuery)) ||
                        (post.getContent() != null && post.getContent().toLowerCase().contains(normalizedQuery))
                    )
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
            long endTime = System.nanoTime();
            recordPerformanceMetric("linear_search", endTime - startTime);
            
            return results;
            
        } catch (Exception e) {
            throw new DatabaseException("LINEAR_SEARCH_ERROR", "Failed to perform linear search", e);
        }
    }
    
    /**
     * Performance comparison between algorithms
     */
    public AlgorithmComparison compareAlgorithms(String query) throws DatabaseException, ValidationException {
        System.out.println("=== Algorithm Performance Comparison ===");
        
        Map<String, Long> executionTimes = new HashMap<>();
        Map<String, List<PostEntity>> results = new HashMap<>();
        
        // Test each algorithm
        String[] algorithms = {"linear", "hash", "binary", "hybrid"};
        
        for (String algorithm : algorithms) {
            try {
                long startTime = System.nanoTime();
                List<PostEntity> algorithmResults;
                
                switch (algorithm) {
                    case "linear":
                        algorithmResults = linearSearch(query);
                        break;
                    case "hash":
                        algorithmResults = hashSearchByKeyword(query);
                        break;
                    case "binary":
                        algorithmResults = binarySearchByTitle(query);
                        break;
                    case "hybrid":
                        algorithmResults = hybridSearch(query);
                        break;
                    default:
                        continue;
                }
                
                long endTime = System.nanoTime();
                executionTimes.put(algorithm, endTime - startTime);
                results.put(algorithm, algorithmResults);
                
            } catch (Exception e) {
                System.err.println("Error testing " + algorithm + ": " + e.getMessage());
            }
        }
        
        // Print comparison
        System.out.println("\nAlgorithm\tTime (μs)\tResults\tRelative Speed");
        System.out.println("---------\t---------\t-------\t--------------");
        
        long baselineTime = executionTimes.getOrDefault("linear", 1L);
        
        for (String algorithm : algorithms) {
            if (executionTimes.containsKey(algorithm)) {
                long time = executionTimes.get(algorithm);
                long resultCount = results.get(algorithm).size();
                double relativeSpeed = (double) baselineTime / time;
                
                System.out.printf("%-9s\t%8d μs\t%7d\t%12.1fx\n", 
                    algorithm, time / 1000, resultCount, relativeSpeed);
            }
        }
        
        return new AlgorithmComparison(executionTimes, results);
    }
    
    /**
     * Index management methods
     */
    private void indexPostByKeywords(PostEntity post) {
        if (post.getTitle() != null) {
            String[] words = post.getTitle().toLowerCase().split("\\s+");
            for (String word : words) {
                if (word.length() > 2) { // Ignore very short words
                    keywordIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(post);
                }
            }
        }
        
        if (post.getContent() != null) {
            String[] words = post.getContent().toLowerCase().split("\\s+");
            for (String word : words) {
                if (word.length() > 2) {
                    keywordIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(post);
                }
            }
        }
    }
    
    private void indexPostByAuthor(PostEntity post) {
        if (post.getAuthorName() != null) {
            String author = post.getAuthorName().toLowerCase();
            authorIndex.computeIfAbsent(author, k -> new ArrayList<>()).add(post);
        }
    }
    
    private void indexPostByTags(PostEntity post) {
        if (post.getContent() != null) {
            String[] tags = post.getContent().toLowerCase().split("#");
            for (int i = 1; i < tags.length; i++) {
                String tag = tags[i].split("\\s+")[0]; // Get first word after #
                if (tag.length() > 0) {
                    tagIndex.computeIfAbsent(tag, k -> new ArrayList<>()).add(post);
                }
            }
        }
    }
    
    private void indexPostByTitle(PostEntity post) {
        if (post.getTitle() != null) {
            String title = post.getTitle().toLowerCase();
            titleIndex.computeIfAbsent(title, k -> new ArrayList<>()).add(post);
        }
    }
    
    private void indexPostById(PostEntity post) {
        postByIdIndex.put(post.getPostId(), post);
    }
    
    private void clearIndexes() {
        keywordIndex.clear();
        authorIndex.clear();
        tagIndex.clear();
        titleIndex.clear();
        postByIdIndex.clear();
    }
    
    private Comparator<PostEntity> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "title" -> Comparator.comparing(PostEntity::getTitle, 
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "views" -> Comparator.comparingInt(PostEntity::getViews);
            case "created" -> Comparator.comparing(PostEntity::getCreatedAt);
            case "author" -> Comparator.comparing(PostEntity::getAuthorName,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            default -> Comparator.comparing(PostEntity::getCreatedAt);
        };
    }
    
    private void recordPerformanceMetric(String algorithm, long executionTime) {
        algorithmPerformanceMetrics.computeIfAbsent(algorithm, k -> new ArrayList<>()).add(executionTime);
    }
    
    /**
     * Get comprehensive performance statistics
     */
    public PerformanceStats getPerformanceStats() {
        Map<String, Double> avgTimes = new HashMap<>();
        Map<String, Long> totalTimes = new HashMap<>();
        Map<String, Integer> executionCounts = new HashMap<>();
        
        for (Map.Entry<String, List<Long>> entry : algorithmPerformanceMetrics.entrySet()) {
            String algorithm = entry.getKey();
            List<Long> times = entry.getValue();
            
            if (!times.isEmpty()) {
                avgTimes.put(algorithm, times.stream().mapToLong(Long::longValue).average().orElse(0.0));
                totalTimes.put(algorithm, times.stream().mapToLong(Long::longValue).sum());
                executionCounts.put(algorithm, times.size());
            }
        }
        
        return new PerformanceStats(
            avgTimes,
            totalTimes,
            executionCounts,
            cacheHits,
            cacheMisses,
            lastIndexUpdate,
            keywordIndex.size(),
            authorIndex.size(),
            tagIndex.size(),
            titleIndex.size()
        );
    }
    
    /**
     * Search options configuration
     */
    public static class SearchOptions {
        private String searchType = "hash";
        private String sortBy = "created";
        private String sortOrder = "desc";
        private int page = 1;
        private int pageSize = 10;
        
        // Getters and setters
        public String getSearchType() { return searchType; }
        public void setSearchType(String searchType) { this.searchType = searchType; }
        
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
        
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    }
    
    /**
     * Search result container
     */
    public static class SearchResult {
        private final List<PostEntity> posts;
        private final int totalResults;
        private final String algorithmUsed;
        private final long executionTime;
        private final SearchOptions options;
        
        public SearchResult(List<PostEntity> posts, int totalResults, String algorithmUsed, 
                          long executionTime, SearchOptions options) {
            this.posts = posts;
            this.totalResults = totalResults;
            this.algorithmUsed = algorithmUsed;
            this.executionTime = executionTime;
            this.options = options;
        }
        
        // Getters
        public List<PostEntity> getPosts() { return posts; }
        public int getTotalResults() { return totalResults; }
        public String getAlgorithmUsed() { return algorithmUsed; }
        public long getExecutionTime() { return executionTime; }
        public SearchOptions getOptions() { return options; }
    }
    
    /**
     * Algorithm comparison result
     */
    public static class AlgorithmComparison {
        private final Map<String, Long> executionTimes;
        private final Map<String, List<PostEntity>> results;
        
        public AlgorithmComparison(Map<String, Long> executionTimes, Map<String, List<PostEntity>> results) {
            this.executionTimes = executionTimes;
            this.results = results;
        }
        
        public Map<String, Long> getExecutionTimes() { return executionTimes; }
        public Map<String, List<PostEntity>> getResults() { return results; }
    }
    
    /**
     * Performance statistics
     */
    public static class PerformanceStats {
        private final Map<String, Double> avgTimes;
        private final Map<String, Long> totalTimes;
        private final Map<String, Integer> executionCounts;
        private final int cacheHits;
        private final int cacheMisses;
        private final LocalDateTime lastIndexUpdate;
        private final int keywordIndexSize;
        private final int authorIndexSize;
        private final int tagIndexSize;
        private final int titleIndexSize;
        
        public PerformanceStats(Map<String, Double> avgTimes, Map<String, Long> totalTimes,
                               Map<String, Integer> executionCounts, int cacheHits, int cacheMisses,
                               LocalDateTime lastIndexUpdate, int keywordIndexSize, int authorIndexSize,
                               int tagIndexSize, int titleIndexSize) {
            this.avgTimes = avgTimes;
            this.totalTimes = totalTimes;
            this.executionCounts = executionCounts;
            this.cacheHits = cacheHits;
            this.cacheMisses = cacheMisses;
            this.lastIndexUpdate = lastIndexUpdate;
            this.keywordIndexSize = keywordIndexSize;
            this.authorIndexSize = authorIndexSize;
            this.tagIndexSize = tagIndexSize;
            this.titleIndexSize = titleIndexSize;
        }
        
        // Getters
        public Map<String, Double> getAvgTimes() { return avgTimes; }
        public Map<String, Long> getTotalTimes() { return totalTimes; }
        public Map<String, Integer> getExecutionCounts() { return executionCounts; }
        public int getCacheHits() { return cacheHits; }
        public int getCacheMisses() { return cacheMisses; }
        public LocalDateTime getLastIndexUpdate() { return lastIndexUpdate; }
        public int getKeywordIndexSize() { return keywordIndexSize; }
        public int getAuthorIndexSize() { return authorIndexSize; }
        public int getTagIndexSize() { return tagIndexSize; }
        public int getTitleIndexSize() { return titleIndexSize; }
        
        public double getCacheHitRate() {
            int total = cacheHits + cacheMisses;
            return total > 0 ? (double) cacheHits / total : 0.0;
        }
    }
}
