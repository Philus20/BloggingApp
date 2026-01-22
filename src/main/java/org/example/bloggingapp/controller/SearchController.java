package org.example.bloggingapp.controller;

import org.example.bloggingapp.Services.PostSearchService;
import org.example.bloggingapp.Services.PostService;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.ValidationException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SearchController {
    
    private final PostSearchService searchService;
    private final PostService postService;
    private final Scanner scanner;
    
    public SearchController(PostSearchService searchService, PostService postService) {
        this.searchService = searchService;
        this.postService = postService;
        this.scanner = new Scanner(System.in);
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
            long startTime = System.currentTimeMillis();
            List<PostEntity> results = searchService.searchByKeyword(keyword);
            long endTime = System.currentTimeMillis();
            
            System.out.println("\nSearch Results for keyword: '" + keyword + "'");
            System.out.println("Found " + results.size() + " posts in " + (endTime - startTime) + "ms");
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
            long startTime = System.currentTimeMillis();
            List<PostEntity> results = searchService.searchByAuthor(author);
            long endTime = System.currentTimeMillis();
            
            System.out.println("\nSearch Results for author: '" + author + "'");
            System.out.println("Found " + results.size() + " posts in " + (endTime - startTime) + "ms");
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
            long startTime = System.currentTimeMillis();
            List<PostEntity> results = searchService.searchByTag(tag);
            long endTime = System.currentTimeMillis();
            
            System.out.println("\nSearch Results for tag: #" + tag);
            System.out.println("Found " + results.size() + " posts in " + (endTime - startTime) + "ms");
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
            long startTime = System.currentTimeMillis();
            List<PostEntity> results = searchService.searchAll(query);
            long endTime = System.currentTimeMillis();
            
            System.out.println("\nCombined Search Results for: '" + query + "'");
            System.out.println("Found " + results.size() + " posts in " + (endTime - startTime) + "ms");
            System.out.println("----------------------------------------");
            
            displaySearchResults(results);
            
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
    
    private void viewPerformanceMetrics() {
        Map<String, Object> metrics = searchService.getPerformanceMetrics();
        
        System.out.println("\n=== Search Performance Metrics ===");
        System.out.println("Total searches: " + metrics.get("totalSearches"));
        System.out.println("Cache hits: " + metrics.get("cacheHits"));
        System.out.println("Cache hit rate: " + String.format("%.2f%%", (Double) metrics.get("cacheHitRate") * 100));
        System.out.println("Average search time: " + String.format("%.2f ms", (Double) metrics.get("averageSearchTimeMs")));
        System.out.println("Last cache update: " + metrics.get("lastCacheUpdate"));
        System.out.println("Keyword cache size: " + metrics.get("keywordCacheSize"));
        System.out.println("Author cache size: " + metrics.get("authorCacheSize"));
        System.out.println("Tag cache size: " + metrics.get("tagCacheSize"));
        System.out.println("----------------------------------------");
    }
    
    private void preloadCache() {
        try {
            System.out.println("Preloading cache with common searches...");
            long startTime = System.currentTimeMillis();
            searchService.preloadCache();
            long endTime = System.currentTimeMillis();
            
            System.out.println("Cache preloaded successfully in " + (endTime - startTime) + "ms");
            viewPerformanceMetrics();
            
        } catch (DatabaseException e) {
            System.out.println("Error preloading cache: " + e.getMessage());
        }
    }
    
    private void clearCache() {
        System.out.print("Are you sure you want to clear all cache? (y/N): ");
        String confirmation = scanner.nextLine();
        
        if ("y".equalsIgnoreCase(confirmation) || "yes".equalsIgnoreCase(confirmation)) {
            searchService.invalidateCache();
            System.out.println("Cache cleared successfully.");
        } else {
            System.out.println("Cache clear cancelled.");
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
                
                List<PostEntity> sortedResults = searchService.sortPosts(results, sortBy, order);
                System.out.println("\n=== Sorted Results ===");
                displaySearchResults(sortedResults);
            }
        }
    }
    
    /**
     * Performance test method to demonstrate search improvements
     */
    public void performanceTest() {
        System.out.println("\n=== Search Performance Test ===");
        
        try {
            // Test without cache
            searchService.invalidateCache();
            System.out.println("Testing search performance without cache...");
            
            String[] testQueries = {"java", "programming", "tutorial", "blog", "post"};
            
            for (String query : testQueries) {
                long startTime = System.nanoTime();
                List<PostEntity> results = searchService.searchByKeyword(query);
                long endTime = System.nanoTime();
                
                System.out.println("Query: '" + query + "' - " + results.size() + " results in " + 
                    (endTime - startTime) / 1_000_000 + "ms");
            }
            
            // Preload cache and test again
            System.out.println("\nPreloading cache...");
            searchService.preloadCache();
            
            System.out.println("Testing search performance with cache...");
            for (String query : testQueries) {
                long startTime = System.nanoTime();
                List<PostEntity> results = searchService.searchByKeyword(query);
                long endTime = System.nanoTime();
                
                System.out.println("Query: '" + query + "' - " + results.size() + " results in " + 
                    (endTime - startTime) / 1_000_000 + "ms (cached)");
            }
            
            System.out.println("\nFinal performance metrics:");
            viewPerformanceMetrics();
            
        } catch (Exception e) {
            System.out.println("Error during performance test: " + e.getMessage());
        }
    }
}
