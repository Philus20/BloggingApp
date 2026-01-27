package org.example.bloggingapp.controller;

import org.example.bloggingapp.Services.AdvancedSearchService;
import org.example.bloggingapp.Services.PostSearchService;
import org.example.bloggingapp.Services.PostService;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Utils.Exceptions.DatabaseException;
import org.example.bloggingapp.Utils.Exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SearchController {
    
    private final PostSearchService searchService;
    private final PostService postService;
    private final Scanner scanner;
    private final AdvancedSearchService advancedSearchService ;
    public SearchController(PostSearchService searchService, PostService postService, AdvancedSearchService advancedSearchService) {
        this.searchService = searchService;
        this.postService = postService;
        this.scanner = new Scanner(System.in);

        this.advancedSearchService = advancedSearchService;
        // Initialize search service
        System.out.println("Search service initialized successfully!");
    }
    
    /**
     * Interactive search menu
     */
    public void searchMenu() {
        while (true) {
            System.out.println("\n=== Post Search Menu ===");
            System.out.println("1. Search by keyword");
            System.out.println("2. Search by author");
            System.out.println("3. Search by tag");
            System.out.println("4. Search all fields");
            System.out.println("5. View performance metrics");
            System.out.println("6. Preload cache");
            System.out.println("7. Clear cache");
            System.out.println("0. Back to main menu");
            System.out.print("Choose option: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        searchByKeyword();
                        break;
                    case 2:
                        searchByAuthor();
                        break;
                    case 3:
                        searchByTag();
                        break;
                    case 4:
                        searchAll();
                        break;
                    case 5:
                        viewPerformanceMetrics();
                        break;
                    case 6:
                        preloadCache();
                        break;
                    case 7:
                        clearCache();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private void searchByKeyword() {
        System.out.print("Enter keyword to search: ");
        String keyword = scanner.nextLine();
        
        try {
            System.out.println("\n=== Using Hash Search Algorithm (O(1) average case) ===");
            long startTime = System.nanoTime();
            List<PostEntity> results = advancedSearchService.hashSearchByKeyword(keyword);
            long endTime = System.nanoTime();
            
            System.out.println("Search Results for keyword: '" + keyword + "'");
            System.out.println("Found " + results.size() + " posts in " + (endTime - startTime) / 1000 + " μs");
            System.out.println("Algorithm: Hash-based search with indexing");
            System.out.println("----------------------------------------");
            
            displaySearchResults(results);
            
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
    
    private void searchByAuthor() {
        System.out.print("Enter author name to search: ");
        String author = scanner.nextLine();
        
        try {
            System.out.println("\n=== Using Hash Search Algorithm for Author Index ===");
            long startTime = System.nanoTime();
            // Using hash search by keyword for author (indexed in authorIndex)
            List<PostEntity> results = advancedSearchService.hashSearchByKeyword(author);
            long endTime = System.nanoTime();
            
            System.out.println("Search Results for author: '" + author + "'");
            System.out.println("Found " + results.size() + " posts in " + (endTime - startTime) / 1000 + " μs");
            System.out.println("Algorithm: Hash-based author index lookup");
            System.out.println("----------------------------------------");
            
            displaySearchResults(results);
            
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
    
    private void searchByTag() {
        System.out.print("Enter tag to search (without #): ");
        String tag = scanner.nextLine();
        
        try {
            System.out.println("\n=== Using Hash Search Algorithm for Tag Index ===");
            long startTime = System.nanoTime();
            // Using hash search by keyword for tag (indexed in tagIndex)
            List<PostEntity> results = advancedSearchService.hashSearchByKeyword(tag);
            long endTime = System.nanoTime();
            
            System.out.println("Search Results for tag: #" + tag);
            System.out.println("Found " + results.size() + " posts in " + (endTime - startTime) / 1000 + " μs");
            System.out.println("Algorithm: Hash-based tag index lookup");
            System.out.println("----------------------------------------");
            
            displaySearchResults(results);
            
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
    
    private void searchAll() {
        System.out.print("Enter search query: ");
        String query = scanner.nextLine();
        
        try {
            System.out.println("\n=== Advanced Search Options ===");
            System.out.println("1. Hash Search (O(1) - Fastest)");
            System.out.println("2. Binary Search (O(log n) - For titles)");
            System.out.println("3. Hybrid Search (Combines multiple algorithms)");
            System.out.println("4. Algorithm Comparison (Test all)");
            System.out.print("Choose search type (1-4): ");
            
            String choice = scanner.nextLine();
            List<PostEntity> results = new ArrayList<>();
            String algorithmUsed = "";
            
            long startTime = System.nanoTime();
            
            switch (choice) {
                case "1":
                    System.out.println("\n=== Using Hash Search Algorithm ===");
                    results = advancedSearchService.hashSearchByKeyword(query);
                    algorithmUsed = "Hash Search (O(1))";
                    break;
                case "2":
                    System.out.println("\n=== Using Binary Search Algorithm ===");
                    results = advancedSearchService.binarySearchByTitle(query);
                    algorithmUsed = "Binary Search (O(log n))";
                    break;
                case "3":
                    System.out.println("\n=== Using Hybrid Search Algorithm ===");
                    // Use advancedSearch with hybrid option
                    AdvancedSearchService.SearchOptions options = new AdvancedSearchService.SearchOptions();
                    options.setSearchType("hybrid");
                    AdvancedSearchService.SearchResult searchResult = advancedSearchService.advancedSearch(query, options);
                    results = searchResult.getPosts();
                    algorithmUsed = "Hybrid Search (Combined)";
                    break;
                case "4":
                    System.out.println("\n=== Algorithm Performance Comparison ===");
                    AdvancedSearchService.AlgorithmComparison comparison = advancedSearchService.compareAlgorithms(query);
                    displayAlgorithmComparison(comparison);
                    return;
                default:
                    System.out.println("\n=== Default: Using Hash Search ===");
                    results = advancedSearchService.hashSearchByKeyword(query);
                    algorithmUsed = "Hash Search (O(1))";
            }
            
            long endTime = System.nanoTime();
            
            System.out.println("\nCombined Search Results for: '" + query + "'");
            System.out.println("Found " + results.size() + " posts in " + (endTime - startTime) / 1000 + " μs");
            System.out.println("Algorithm Used: " + algorithmUsed);
            System.out.println("----------------------------------------");
            
            displaySearchResults(results);
            
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
    
    public void viewPerformanceMetrics() {
        AdvancedSearchService.PerformanceStats stats = advancedSearchService.getPerformanceStats();
        
        System.out.println("\n=== Advanced Search Performance Metrics ===");
        System.out.println("Cache hits: " + stats.getCacheHits());
        System.out.println("Cache misses: " + stats.getCacheMisses());
        System.out.println("Cache hit rate: " + String.format("%.2f%%", stats.getCacheHitRate() * 100));
        System.out.println("Last index update: " + stats.getLastIndexUpdate());
        System.out.println("Keyword index size: " + stats.getKeywordIndexSize() + " entries");
        System.out.println("Author index size: " + stats.getAuthorIndexSize() + " entries");
        System.out.println("Tag index size: " + stats.getTagIndexSize() + " entries");
        System.out.println("Title index size: " + stats.getTitleIndexSize() + " entries");
        
        // Show algorithm performance averages
        System.out.println("\nAlgorithm Performance Averages:");
        Map<String, Double> avgTimes = stats.getAvgTimes();
        for (Map.Entry<String, Double> entry : avgTimes.entrySet()) {
            System.out.printf("%s: %.1f μs\n", entry.getKey(), entry.getValue() / 1000);
        }
        System.out.println("----------------------------------------");
    }
    
    public void preloadCache() {
        try {
            System.out.println("Building advanced search indexes...");
            long startTime = System.currentTimeMillis();
            advancedSearchService.buildIndexes();
            long endTime = System.currentTimeMillis();
            
            System.out.println("Indexes built successfully in " + (endTime - startTime) + "ms");
            viewPerformanceMetrics();
            
        } catch (DatabaseException e) {
            System.out.println("Error building indexes: " + e.getMessage());
        }
    }
    
    private void clearCache() {
        System.out.print("Are you sure you want to clear all indexes? (y/N): ");
        String confirmation = scanner.nextLine();
        
        if ("y".equalsIgnoreCase(confirmation) || "yes".equalsIgnoreCase(confirmation)) {
            // Clear indexes by rebuilding empty ones
            try {
                advancedSearchService.buildIndexes(); // Rebuild indexes
                System.out.println("Indexes rebuilt successfully.");
            } catch (DatabaseException e) {
                System.out.println("Error rebuilding indexes: " + e.getMessage());
            }
        } else {
            System.out.println("Index rebuild cancelled.");
        }
    }
    
    private void displaySearchResults(List<PostEntity> results) {
        if (results.isEmpty()) {
            System.out.println("No posts found matching your search criteria.");
            return;
        }
        
        for (int i = 0; i < results.size(); i++) {
            PostEntity post = results.get(i);
            System.out.println("\n" + (i + 1) + ". " + post.getTitle());
            System.out.println("   Author: " + (post.getAuthorName() != null ? post.getAuthorName() : "Unknown"));
            System.out.println("   Created: " + post.getCreatedAt());
            System.out.println("   Views: " + post.getViews());
            System.out.println("   Status: " + post.getStatus());
            
            // Display content preview (first 100 characters)
            String content = post.getContent();
            if (content != null && content.length() > 100) {
                content = content.substring(0, 100) + "...";
            }
            System.out.println("   Preview: " + content);
            System.out.println("   ---");
        }
        
        // Ask if user wants to sort results
        if (results.size() > 1) {
            System.out.print("\nSort results by (title/views/created/author) or press Enter to skip: ");
            String sortBy = scanner.nextLine();
            
            if (!sortBy.trim().isEmpty()) {
                System.out.print("Order (asc/desc) [default: desc]: ");
                String order = scanner.nextLine();
                if (order.trim().isEmpty()) {
                    order = "desc";
                }
                
                List<PostEntity> sortedResults = advancedSearchService.quickSortPosts(results, sortBy, order);
                System.out.println("\n=== Results Sorted with QuickSort Algorithm (O(n log n)) ===");
                System.out.println("Sort by: " + sortBy + " (" + order + ")");
                displaySearchResults(sortedResults);
            }
        }
    }
    
    /**
     * Display algorithm comparison results
     */
    private void displayAlgorithmComparison(AdvancedSearchService.AlgorithmComparison comparison) {
        System.out.println("\n=== Algorithm Performance Comparison ===");
        System.out.println("Algorithm\t\tTime (μs)\tResults\tRelative Speed");
        System.out.println("---------\t\t---------\t-------\t--------------");
        
        Map<String, Long> times = comparison.getExecutionTimes();
        Map<String, List<PostEntity>> results = comparison.getResults();
        
        long baselineTime = times.getOrDefault("linear", 1L);
        
        for (String algorithm : java.util.Arrays.asList("linear", "hash", "binary", "hybrid")) {
            if (times.containsKey(algorithm)) {
                long time = times.get(algorithm);
                long resultCount = results.get(algorithm).size();
                double relativeSpeed = (double) baselineTime / time;
                
                System.out.printf("%-9s\t\t%8d μs\t%7d\t%12.1fx\n", 
                    algorithm, time / 1000, resultCount, relativeSpeed);
            }
        }
        
        System.out.println("\nPerformance Analysis:");
        System.out.println("• Hash Search: O(1) average case - Fastest for keyword lookups");
        System.out.println("• Binary Search: O(log n) - Optimal for sorted title searches");
        System.out.println("• Hybrid Search: Combines multiple strategies for best results");
        System.out.println("• Linear Search: O(n) - Baseline comparison");
        System.out.println("----------------------------------------");
    }
    /**
     * Performance test method to demonstrate advanced search improvements
     */
    public void performanceTest() {
        System.out.println("\n=== Advanced Search Performance Test ===");
        
        try {
            String[] testQueries = {"java", "programming", "tutorial", "blog", "post"};
            
            System.out.println("Testing all algorithms with performance comparison...");
            
            for (String query : testQueries) {
                System.out.println("\nTesting query: '" + query + "'");
                AdvancedSearchService.AlgorithmComparison comparison = advancedSearchService.compareAlgorithms(query);
                displayAlgorithmComparison(comparison);
            }
            
            System.out.println("\n=== QuickSort Performance Test ===");
            List<PostEntity> allPosts = postService.findAll();
            if (!allPosts.isEmpty()) {
                String[] sortCriteria = {"title", "views", "created", "author"};
                
                for (String sortBy : sortCriteria) {
                    long startTime = System.nanoTime();
                    List<PostEntity> sorted = advancedSearchService.quickSortPosts(allPosts, sortBy, "desc");
                    long endTime = System.nanoTime();
                    
                    System.out.printf("QuickSort by %s: %d posts in %d μs\n", 
                        sortBy, sorted.size(), (endTime - startTime) / 1000);
                }
            }
            
            System.out.println("\nFinal advanced search performance metrics:");
            viewPerformanceMetrics();
            
        } catch (Exception e) {
            System.out.println("Error during advanced performance test: " + e.getMessage());
        }
    }
}
