package org.example.bloggingapp.Demo;

import org.example.bloggingapp.Database.Repositories.PostRepository;
import org.example.bloggingapp.Database.Services.PostService;
import org.example.bloggingapp.Database.Services.AdvancedSearchService;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Comprehensive demonstration of Data Structures & Algorithms Integration
 * Shows hashing, caching, sorting, searching, and indexing concepts
 */
public class DataStructuresAlgorithmsDemo {
    
    private final PostService postService;
    private final AdvancedSearchService advancedSearchService;
    private final Scanner scanner;
    
    public DataStructuresAlgorithmsDemo() {
        PostRepository postRepository = new PostRepository();
        this.postService = new PostService(postRepository);
        this.advancedSearchService = new AdvancedSearchService(postService);
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        DataStructuresAlgorithmsDemo demo = new DataStructuresAlgorithmsDemo();
        demo.runComprehensiveDemo();
    }
    
    public void runComprehensiveDemo() {
        System.out.println("=== Data Structures & Algorithms Integration Demo ===");
        
        try {
            // Initialize sample data
            initializeComprehensiveTestData();
            
            // Main demo menu
            while (true) {
                System.out.println("\n=== DS&A Algorithms Demo Menu ===");
                System.out.println("1. Hashing & Caching Demonstration");
                System.out.println("2. Sorting Algorithms (QuickSort)");
                System.out.println("3. Search Algorithms (Binary vs Linear vs Hash)");
                System.out.println("4. Indexing Concepts Explanation");
                System.out.println("5. Performance Measurement & Comparison");
                System.out.println("6. Interactive Advanced Search");
                System.out.println("7. Comprehensive Algorithm Analysis");
                System.out.println("0. Exit");
                System.out.print("Choose option: ");
                
                try {
                    int choice = Integer.parseInt(scanner.nextLine());
                    
                    switch (choice) {
                        case 1:
                            demonstrateHashingAndCaching();
                            break;
                        case 2:
                            demonstrateSortingAlgorithms();
                            break;
                        case 3:
                            demonstrateSearchAlgorithms();
                            break;
                        case 4:
                            explainIndexingConcepts();
                            break;
                        case 5:
                            demonstratePerformanceMeasurement();
                            break;
                        case 6:
                            interactiveAdvancedSearch();
                            break;
                        case 7:
                            comprehensiveAlgorithmAnalysis();
                            break;
                        case 0:
                            System.out.println("Thank you for using the DS&A Demo!");
                            return;
                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeComprehensiveTestData() throws DatabaseException {
        System.out.println("Initializing comprehensive test data for DS&A demonstration...");
        
        // Create diverse sample posts for algorithm testing
        List<PostEntity> comprehensivePosts = Arrays.asList(
            new PostEntity(1, "Data Structures Fundamentals", 
                "Learn about arrays, linked lists, stacks, and queues. Essential #datastructures for #programming.", 
                LocalDateTime.now().minusDays(20), 1, "Published", 300, "Dr. Smith"),
            new PostEntity(2, "Algorithm Analysis", 
                "Understanding Big O notation and algorithm complexity. #algorithms #analysis #performance", 
                LocalDateTime.now().minusDays(18), 2, "Published", 250, "Prof. Johnson"),
            new PostEntity(3, "Hash Tables Explained", 
                "Deep dive into hash tables, collision resolution, and performance. #hashing #datastructures", 
                LocalDateTime.now().minusDays(16), 1, "Published", 400, "Dr. Smith"),
            new PostEntity(4, "Binary Search Trees", 
                "Complete guide to BST operations and balancing. #trees #algorithms #searching", 
                LocalDateTime.now().minusDays(14), 3, "Published", 350, "Dr. Williams"),
            new PostEntity(5, "Sorting Algorithms Comparison", 
                "QuickSort vs MergeSort vs HeapSort performance analysis. #sorting #algorithms", 
                LocalDateTime.now().minusDays(12), 2, "Published", 450, "Prof. Johnson"),
            new PostEntity(6, "Graph Algorithms", 
                "DFS, BFS, Dijkstra's algorithm and graph traversal. #graphs #algorithms #searching", 
                LocalDateTime.now().minusDays(10), 3, "Published", 380, "Dr. Williams"),
            new PostEntity(7, "Dynamic Programming", 
                "Master DP techniques with classic problems. #dp #algorithms #optimization", 
                LocalDateTime.now().minusDays(8), 1, "Published", 420, "Dr. Smith"),
            new PostEntity(8, "Caching Strategies", 
                "LRU, LFU, and other caching algorithms explained. #caching #performance #systems", 
                LocalDateTime.now().minusDays(6), 2, "Published", 320, "Prof. Johnson"),
            new PostEntity(9, "Database Indexing", 
                "How database indexes work and improve query performance. #database #indexing #performance", 
                LocalDateTime.now().minusDays(4), 3, "Published", 500, "Dr. Williams"),
            new PostEntity(10, "Advanced Search Techniques", 
                "Full-text search, inverted indexes, and search algorithms. #search #algorithms #indexing", 
                LocalDateTime.now().minusDays(2), 1, "Published", 480, "Dr. Smith"),
            new PostEntity(11, "Memory Management", 
                "Memory allocation, garbage collection, and optimization. #memory #performance #systems", 
                LocalDateTime.now().minusDays(1), 2, "Published", 360, "Prof. Johnson"),
            new PostEntity(12, "Concurrent Data Structures", 
                "Thread-safe data structures and concurrent algorithms. #concurrency #datastructures", 
                LocalDateTime.now(), 3, "Published", 440, "Dr. Williams")
        );
        
        // Add posts to repository
        for (PostEntity post : comprehensivePosts) {
            try {
                postService.create(post);
            } catch (Exception e) {
                // Post might already exist, continue
            }
        }
        
        // Build advanced indexes
        advancedSearchService.buildIndexes();
        
        System.out.println("Comprehensive test data initialized successfully!");
        System.out.println("Created " + comprehensivePosts.size() + " posts for DS&A demonstration");
    }
    
    private void demonstrateHashingAndCaching() {
        System.out.println("\n=== Hashing & Caching Demonstration ===");
        
        try {
            System.out.println("\n1. Hash-based Search Performance:");
            
            String[] testQueries = {"algorithms", "datastructures", "performance", "hashing"};
            
            for (String query : testQueries) {
                // First search (cache miss)
                long startTime = System.nanoTime();
                List<PostEntity> results1 = advancedSearchService.hashSearchByKeyword(query);
                long firstTime = System.nanoTime() - startTime;
                
                // Second search (cache hit)
                startTime = System.nanoTime();
                List<PostEntity> results2 = advancedSearchService.hashSearchByKeyword(query);
                long secondTime = System.nanoTime() - startTime;
                
                System.out.printf("Query: '%s'\n", query);
                System.out.printf("  First search: %d μs (cache miss)\n", firstTime / 1000);
                System.out.printf("  Second search: %d μs (cache hit)\n", secondTime / 1000);
                System.out.printf("  Speedup: %.1fx\n", (double) firstTime / secondTime);
                System.out.printf("  Results: %d posts\n\n", results1.size());
            }
            
            System.out.println("2. Hash Index Statistics:");
            AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
            System.out.printf("Cache hit rate: %.1f%%\n", stats.getCacheHitRate() * 100);
            System.out.printf("Keyword index size: %d entries\n", stats.getKeywordIndexSize());
            System.out.printf("Author index size: %d entries\n", stats.getAuthorIndexSize());
            System.out.printf("Tag index size: %d entries\n", stats.getTagIndexSize());
            
        } catch (Exception e) {
            System.out.println("Error in hashing demonstration: " + e.getMessage());
        }
    }
    
    private void demonstrateSortingAlgorithms() {
        System.out.println("\n=== Sorting Algorithms Demonstration (QuickSort) ===");
        
        try {
            // Get all posts for sorting
            List<PostEntity> allPosts = postService.findAll();
            
            System.out.println("Sorting " + allPosts.size() + " posts using QuickSort...");
            
            String[] sortCriteria = {"title", "views", "created", "author"};
            String[] orders = {"asc", "desc"};
            
            for (String sortBy : sortCriteria) {
                for (String order : orders) {
                    long startTime = System.nanoTime();
                    List<PostEntity> sorted = advancedSearchService.quickSortPosts(allPosts, sortBy, order);
                    long endTime = System.nanoTime();
                    
                    System.out.printf("\nSort by %s (%s): %d μs\n", sortBy, order, (endTime - startTime) / 1000);
                    
                    // Show top 3 results
                    for (int i = 0; i < Math.min(3, sorted.size()); i++) {
                        PostEntity post = sorted.get(i);
                        String displayValue = switch (sortBy) {
                            case "title" -> post.getTitle();
                            case "views" -> String.valueOf(post.getViews());
                            case "created" -> post.getCreatedAt().toString();
                            case "author" -> post.getAuthorName();
                            default -> post.getTitle();
                        };
                        System.out.printf("  %d. %s\n", i + 1, displayValue);
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error in sorting demonstration: " + e.getMessage());
        }
    }
    
    private void demonstrateSearchAlgorithms() {
        System.out.println("\n=== Search Algorithms Comparison ===");
        
        try {
            System.out.print("Enter search query for algorithm comparison: ");
            String query = scanner.nextLine();
            
            if (query.trim().isEmpty()) {
                query = "algorithms"; // Default query
            }
            
            AdvancedSearchService.AlgorithmComparison comparison = advancedSearchService.compareAlgorithms(query);
            
            System.out.println("\nDetailed Analysis:");
            Map<String, Long> times = comparison.getExecutionTimes();
            Map<String, List<PostEntity>> results = comparison.getResults();
            
            // Find fastest algorithm
            String fastest = times.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
            
            System.out.printf("\nFastest algorithm: %s\n", fastest);
            
            // Show results for each algorithm
            for (String algorithm : results.keySet()) {
                List<PostEntity> algResults = results.get(algorithm);
                System.out.printf("\n%s Search Results (%d posts):\n", 
                    algorithm.substring(0, 1).toUpperCase() + algorithm.substring(1), 
                    algResults.size());
                
                for (int i = 0; i < Math.min(3, algResults.size()); i++) {
                    PostEntity post = algResults.get(i);
                    System.out.printf("  %d. %s\n", i + 1, post.getTitle());
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error in search algorithms demonstration: " + e.getMessage());
        }
    }
    
    private void explainIndexingConcepts() {
        System.out.println("\n=== Indexing Concepts Explanation ===");
        
        System.out.println("\n1. Database Indexing vs In-Memory Indexing:");
        System.out.println("   • Database Indexing:");
        System.out.println("     - B-Tree or B+-Tree structures on disk");
        System.out.println("     - Persistent storage, slower I/O");
        System.out.println("     - Optimized for disk-based access patterns");
        System.out.println("     - Maintained by database engine");
        
        System.out.println("\n   • In-Memory Indexing (Our Implementation):");
        System.out.println("     - HashMap, TreeMap, and other memory structures");
        System.out.println("     - RAM-based, much faster access");
        System.out.println("     - Hash-based O(1) lookups for keyword search");
        System.out.println("     - Tree-based O(log n) lookups for sorted search");
        
        System.out.println("\n2. Index Types in Our Implementation:");
        
        AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
        
        System.out.printf("\n   • Keyword Index (Hash-based): %d entries\n", stats.getKeywordIndexSize());
        System.out.println("     - Inverted index concept");
        System.out.println("     - Maps keywords to list of posts");
        System.out.println("     - O(1) average lookup time");
        
        System.out.printf("\n   • Title Index (Tree-based): %d entries\n", stats.getTitleIndexSize());
        System.out.println("     - TreeMap for sorted access");
        System.out.println("     - Enables binary search");
        System.out.println("     - O(log n) lookup time");
        
        System.out.printf("\n   • Author Index (Hash-based): %d entries\n", stats.getAuthorIndexSize());
        System.out.println("     - Maps author names to posts");
        System.out.println("     - Fast author-based searches");
        
        System.out.printf("\n   • Tag Index (Hash-based): %d entries\n", stats.getTagIndexSize());
        System.out.println("     - Extracts hashtags from content");
        System.out.println("     - Enables tag-based filtering");
        
        System.out.println("\n3. Index Building Process:");
        System.out.println("   • Single-pass through all posts");
        System.out.println("   • Extract and normalize indexable content");
        System.out.println("   • Populate multiple index structures");
        System.out.println("   • Trade-off: Build time vs Query time");
        
        System.out.println("\n4. Cache Invalidation Strategy:");
        System.out.println("   • Automatic invalidation on data changes");
        System.out.println("   • Ensures index consistency");
        System.out.println("   • Rebuild indexes when needed");
    }
    
    private void demonstratePerformanceMeasurement() {
        System.out.println("\n=== Performance Measurement & Comparison ===");
        
        try {
            System.out.println("Running comprehensive performance analysis...");
            
            // Test different query complexities
            String[] queries = {
                "single",           // Simple query
                "data structures",  // Multi-word query
                "performance optimization", // Complex query
                "hashing algorithms caching" // Very complex query
            };
            
            System.out.println("\nQuery Performance Analysis:");
            System.out.println("Query Type\t\tLinear\t\tHash\t\tBinary\t\tHybrid");
            System.out.println("----------\t\t------\t\t----\t\t------\t\t------");
            
            for (String query : queries) {
                AdvancedSearchService.AlgorithmComparison comparison = advancedSearchService.compareAlgorithms(query);
                Map<String, Long> times = comparison.getExecutionTimes();
                
                System.out.printf("%-20s\t", query);
                System.out.printf("%8.1f\t", times.getOrDefault("linear", 0L) / 1000.0);
                System.out.printf("%8.1f\t", times.getOrDefault("hash", 0L) / 1000.0);
                System.out.printf("%8.1f\t", times.getOrDefault("binary", 0L) / 1000.0);
                System.out.printf("%8.1f\n", times.getOrDefault("hybrid", 0L) / 1000.0);
            }
            
            // Show overall performance statistics
            AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
            
            System.out.println("\nOverall Performance Statistics:");
            System.out.printf("Total cache hits: %d\n", stats.getCacheHits());
            System.out.printf("Total cache misses: %d\n", stats.getCacheMisses());
            System.out.printf("Cache hit rate: %.1f%%\n", stats.getCacheHitRate() * 100);
            System.out.printf("Last index update: %s\n", stats.getLastIndexUpdate());
            
            System.out.println("\nAlgorithm Average Execution Times:");
            for (Map.Entry<String, Double> entry : stats.getAvgTimes().entrySet()) {
                System.out.printf("%s: %.1f μs\n", entry.getKey(), entry.getValue() / 1000.0);
            }
            
        } catch (Exception e) {
            System.out.println("Error in performance measurement: " + e.getMessage());
        }
    }
    
    private void interactiveAdvancedSearch() {
        System.out.println("\n=== Interactive Advanced Search ===");
        
        try {
            AdvancedSearchService.SearchOptions options = new AdvancedSearchService.SearchOptions();
            
            // Configure search options
            System.out.println("Configure search options:");
            
            System.out.print("Search type (linear/hash/binary/hybrid) [default: hash]: ");
            String searchType = scanner.nextLine();
            if (!searchType.trim().isEmpty()) {
                options.setSearchType(searchType);
            }
            
            System.out.print("Sort by (title/views/created/author) [default: created]: ");
            String sortBy = scanner.nextLine();
            if (!sortBy.trim().isEmpty()) {
                options.setSortBy(sortBy);
            }
            
            System.out.print("Sort order (asc/desc) [default: desc]: ");
            String sortOrder = scanner.nextLine();
            if (!sortOrder.trim().isEmpty()) {
                options.setSortOrder(sortOrder);
            }
            
            System.out.print("Page number [default: 1]: ");
            String pageStr = scanner.nextLine();
            if (!pageStr.trim().isEmpty()) {
                options.setPage(Integer.parseInt(pageStr));
            }
            
            System.out.print("Page size [default: 10]: ");
            String pageSizeStr = scanner.nextLine();
            if (!pageSizeStr.trim().isEmpty()) {
                options.setPageSize(Integer.parseInt(pageSizeStr));
            }
            
            // Perform search
            System.out.print("Enter search query: ");
            String query = scanner.nextLine();
            
            if (query.trim().isEmpty()) {
                System.out.println("Query cannot be empty. Using default query 'algorithms'");
                query = "algorithms";
            }
            
            long startTime = System.currentTimeMillis();
            AdvancedSearchService.SearchResult result = advancedSearchService.advancedSearch(query, options);
            long endTime = System.currentTimeMillis();
            
            // Display results
            System.out.println("\n=== Search Results ===");
            System.out.printf("Query: '%s'\n", query);
            System.out.printf("Algorithm: %s\n", result.getAlgorithmUsed());
            System.out.printf("Total results: %d\n", result.getTotalResults());
            System.out.printf("Page %d of %d\n", options.getPage(), 
                (int) Math.ceil((double) result.getTotalResults() / options.getPageSize()));
            System.out.printf("Execution time: %d ms\n", result.getExecutionTime() / 1_000_000);
            System.out.printf("Total time including display: %d ms\n", endTime - startTime);
            
            System.out.println("\nResults:");
            for (int i = 0; i < result.getPosts().size(); i++) {
                PostEntity post = result.getPosts().get(i);
                System.out.printf("%d. %s\n", (options.getPage() - 1) * options.getPageSize() + i + 1, post.getTitle());
                System.out.printf("   Author: %s\n", post.getAuthorName());
                System.out.printf("   Views: %d\n", post.getViews());
                System.out.printf("   Created: %s\n", post.getCreatedAt().toLocalDate());
                System.out.println();
            }
            
        } catch (Exception e) {
            System.out.println("Error in interactive search: " + e.getMessage());
        }
    }
    
    private void comprehensiveAlgorithmAnalysis() {
        System.out.println("\n=== Comprehensive Algorithm Analysis ===");
        
        try {
            // Test scalability with different dataset sizes
            System.out.println("1. Scalability Analysis:");
            
            List<PostEntity> allPosts = postService.findAll();
            int[] testSizes = {5, 10, 25, 50};
            
            System.out.println("\nDataset Size\tLinear\t\tHash\t\tBinary\t\tQuickSort");
            System.out.println("------------\t------\t\t----\t\t------\t\t--------");
            
            for (int size : testSizes) {
                if (size > allPosts.size()) continue;
                
                List<PostEntity> subset = allPosts.subList(0, size);
                
                // Test linear search
                long linearTime = measureLinearSearch(subset, "algorithms");
                
                // Test hash search (rebuild index for subset)
                long hashTime = measureHashSearch(subset, "algorithms");
                
                // Test binary search
                long binaryTime = measureBinarySearch(subset, "algorithms");
                
                // Test QuickSort
                long quickSortTime = measureQuickSort(subset);
                
                System.out.printf("%-12d\t%8.1f\t%8.1f\t%8.1f\t%8.1f\n",
                    size, linearTime / 1000.0, hashTime / 1000.0, 
                    binaryTime / 1000.0, quickSortTime / 1000.0);
            }
            
            // Time complexity analysis
            System.out.println("\n2. Time Complexity Analysis:");
            System.out.println("Algorithm\t\tTheoretical\t\tPractical\t\tEfficiency");
            System.out.println("---------\t\t-----------\t\t---------\t\t----------");
            
            AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
            
            Map<String, String> complexities = Map.of(
                "linear_search", "O(n)",
                "hash_search_hit", "O(1)",
                "hash_search_miss", "O(1)",
                "binary_search", "O(log n)",
                "quicksort", "O(n log n)"
            );
            
            for (Map.Entry<String, String> entry : complexities.entrySet()) {
                String algorithm = entry.getKey();
                String theoretical = entry.getValue();
                Double practical = stats.getAvgTimes().get(algorithm);
                
                if (practical != null) {
                    String efficiency = getEfficiencyRating(theoretical, practical);
                    System.out.printf("%-15s\t%12s\t%12.1f μs\t%12s\n",
                        algorithm, theoretical, practical / 1000.0, efficiency);
                }
            }
            
            // Memory usage analysis
            System.out.println("\n3. Memory Usage Analysis:");
            System.out.printf("Keyword Index: ~%d KB\n", estimateMemoryUsage(stats.getKeywordIndexSize(), 50));
            System.out.printf("Author Index: ~%d KB\n", estimateMemoryUsage(stats.getAuthorIndexSize(), 30));
            System.out.printf("Tag Index: ~%d KB\n", estimateMemoryUsage(stats.getTagIndexSize(), 20));
            System.out.printf("Title Index: ~%d KB\n", estimateMemoryUsage(stats.getTitleIndexSize(), 40));
            
            System.out.printf("\nTotal estimated memory: ~%d KB\n",
                estimateMemoryUsage(stats.getKeywordIndexSize(), 50) +
                estimateMemoryUsage(stats.getAuthorIndexSize(), 30) +
                estimateMemoryUsage(stats.getTagIndexSize(), 20) +
                estimateMemoryUsage(stats.getTitleIndexSize(), 40));
            
        } catch (Exception e) {
            System.out.println("Error in comprehensive analysis: " + e.getMessage());
        }
    }
    
    private long measureLinearSearch(List<PostEntity> posts, String query) {
        long startTime = System.nanoTime();
        
        String normalizedQuery = query.toLowerCase();
        for (PostEntity post : posts) {
            if (post.getTitle() != null && post.getTitle().toLowerCase().contains(normalizedQuery)) {
                break; // Found first match
            }
        }
        
        return System.nanoTime() - startTime;
    }
    
    private long measureHashSearch(List<PostEntity> posts, String query) {
        // Simulate hash index creation and lookup
        Map<String, List<PostEntity>> tempIndex = new HashMap<>();
        
        // Build index
        for (PostEntity post : posts) {
            if (post.getTitle() != null) {
                String[] words = post.getTitle().toLowerCase().split("\\s+");
                for (String word : words) {
                    tempIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(post);
                }
            }
        }
        
        // Search
        long startTime = System.nanoTime();
        List<PostEntity> results = tempIndex.get(query.toLowerCase());
        long endTime = System.nanoTime();
        
        return endTime - startTime;
    }
    
    private long measureBinarySearch(List<PostEntity> posts, String query) {
        // Sort posts by title for binary search
        posts.sort(Comparator.comparing(PostEntity::getTitle, 
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        
        long startTime = System.nanoTime();
        
        // Simple binary search simulation
        int left = 0, right = posts.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            PostEntity midPost = posts.get(mid);
            String title = midPost.getTitle();
            
            if (title != null && title.toLowerCase().contains(query.toLowerCase())) {
                break; // Found
            }
            
            if (title == null || title.compareToIgnoreCase(query) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return System.nanoTime() - startTime;
    }
    
    private long measureQuickSort(List<PostEntity> posts) {
        List<PostEntity> copy = new ArrayList<>(posts);
        
        long startTime = System.nanoTime();
        advancedSearchService.quickSortPosts(copy, "title", "asc");
        long endTime = System.nanoTime();
        
        return endTime - startTime;
    }
    
    private String getEfficiencyRating(String theoretical, Double practical) {
        if (practical == null) return "Unknown";
        
        // Simple efficiency rating based on practical performance
        double timeMicros = practical / 1000.0;
        
        if (theoretical.equals("O(1)") && timeMicros < 10) return "Excellent";
        if (theoretical.equals("O(1)") && timeMicros < 50) return "Good";
        if (theoretical.equals("O(log n)") && timeMicros < 20) return "Excellent";
        if (theoretical.equals("O(log n)") && timeMicros < 100) return "Good";
        if (theoretical.equals("O(n)") && timeMicros < 100) return "Excellent";
        if (theoretical.equals("O(n)") && timeMicros < 500) return "Good";
        if (theoretical.equals("O(n log n)") && timeMicros < 200) return "Excellent";
        if (theoretical.equals("O(n log n)") && timeMicros < 1000) return "Good";
        
        return "Fair";
    }
    
    private int estimateMemoryUsage(int entries, int avgBytesPerEntry) {
        return (entries * avgBytesPerEntry) / 1024; // Convert to KB
    }
}
