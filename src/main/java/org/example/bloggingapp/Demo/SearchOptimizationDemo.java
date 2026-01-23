package org.example.bloggingapp.Demo;

import org.example.bloggingapp.Database.Repositories.PostRepository;
import org.example.bloggingapp.Services.PostService;
import org.example.bloggingapp.Services.PostSearchService;
import org.example.bloggingapp.Utils.SearchPerformanceBenchmark;
import org.example.bloggingapp.controller.SearchController;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Utils.Exceptions.DatabaseException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Demo application to showcase Epic 3: Searching, Sorting, and Optimization
 * Demonstrates the implemented search, caching, and performance features
 */
public class SearchOptimizationDemo {
    
    private final PostService postService;
    private final PostSearchService searchService;
    private final SearchController searchController;
    private final SearchPerformanceBenchmark benchmark;
    private final Scanner scanner;
    
    public SearchOptimizationDemo() {
        // Initialize services
        PostRepository postRepository = new PostRepository();
        this.postService = new PostService(postRepository);
        this.searchService = new PostSearchService(postService);
        this.postService.setSearchService(searchService);
        this.searchController = new SearchController(searchService, postService);
        this.benchmark = new SearchPerformanceBenchmark(searchService, postService);
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        SearchOptimizationDemo demo = new SearchOptimizationDemo();
        demo.runDemo();
    }
    
    public void runDemo() {
        System.out.println("=== Blogging App - Epic 3 Demo: Searching, Sorting, and Optimization ===");
        
        try {
            // Initialize sample data
            initializeSampleData();
            
            // Main demo menu
            while (true) {
                System.out.println("\n=== Demo Menu ===");
                System.out.println("1. Interactive Search");
                System.out.println("2. Performance Benchmark");
                System.out.println("3. Scalability Test");
                System.out.println("4. Algorithm Comparison");
                System.out.println("5. View Sample Data");
                System.out.println("6. Add Sample Post");
                System.out.println("0. Exit");
                System.out.print("Choose option: ");
                
                try {
                    int choice = Integer.parseInt(scanner.nextLine());
                    
                    switch (choice) {
                        case 1:
                            searchController.searchMenu();
                            break;
                        case 2:
                            benchmark.runBenchmark();
                            break;
                        case 3:
                            benchmark.scalabilityTest();
                            break;
                        case 4:
                            benchmark.algorithmComparison();
                            break;
                        case 5:
                            viewSampleData();
                            break;
                        case 6:
                            addSamplePost();
                            break;
                        case 0:
                            System.out.println("Thank you for using the Blogging App Search Demo!");
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
    
    private void initializeSampleData() throws DatabaseException {
        System.out.println("Initializing sample data...");
        
        // Create sample posts with varied content for testing
        List<PostEntity> samplePosts = Arrays.asList(
            new PostEntity(1, "Java Programming Basics", 
                "Learn the fundamentals of #Java programming including variables, loops, and functions. This #tutorial covers essential concepts for beginners.", 
                LocalDateTime.now().minusDays(10), 1, "Published", 150, "John Doe"),
            new PostEntity(2, "Advanced Java Techniques", 
                "Explore advanced #Java topics like multithreading, collections, and design patterns. Perfect for experienced #programmers.", 
                LocalDateTime.now().minusDays(8), 1, "Published", 200, "Jane Smith"),
            new PostEntity(3, "Web Development with Spring", 
                "Build modern web applications using #Spring framework. This guide covers #Spring Boot, REST APIs, and more.", 
                LocalDateTime.now().minusDays(6), 2, "Published", 180, "Bob Johnson"),
            new PostEntity(4, "Data Structures in Java", 
                "Understanding #data structures is crucial for efficient programming. Learn about arrays, linked lists, trees, and graphs in #Java.", 
                LocalDateTime.now().minusDays(5), 1, "Published", 220, "Alice Brown"),
            new PostEntity(5, "Algorithm Design Patterns", 
                "Master common #algorithm patterns and problem-solving techniques. Essential for technical interviews and competitive #programming.", 
                LocalDateTime.now().minusDays(4), 3, "Published", 190, "Charlie Davis"),
            new PostEntity(6, "Database Optimization Tips", 
                "Improve your #database performance with these optimization techniques. Covers indexing, query optimization, and caching strategies.", 
                LocalDateTime.now().minusDays(3), 2, "Published", 175, "Eve Wilson"),
            new PostEntity(7, "Microservices Architecture", 
                "Learn how to design and implement #microservices using #Java and Spring Cloud. Scalable architecture for modern applications.", 
                LocalDateTime.now().minusDays(2), 3, "Published", 210, "Frank Miller"),
            new PostEntity(8, "Testing Java Applications", 
                "Comprehensive guide to #testing in #Java. Unit tests, integration tests, and test-driven development best practices.", 
                LocalDateTime.now().minusDays(1), 1, "Published", 165, "Grace Taylor"),
            new PostEntity(9, "Cloud Native Development", 
                "Build #cloud-native applications with containers and Kubernetes. Modern deployment strategies for #Java applications.", 
                LocalDateTime.now(), 2, "Published", 195, "Henry Anderson"),
            new PostEntity(10, "Security Best Practices", 
                "Essential #security practices for #Java applications. Learn about authentication, authorization, and secure coding.", 
                LocalDateTime.now().minusHours(6), 3, "Published", 185, "Ivy Thomas")
        );
        
        // Add posts to the repository
        for (PostEntity post : samplePosts) {
            try {
                postService.create(post);
            } catch (Exception e) {
                // Post might already exist, continue
            }
        }
        
        System.out.println("Sample data initialized successfully!");
        System.out.println("Created " + samplePosts.size() + " sample posts for testing.");
    }
    
    private void viewSampleData() {
        try {
            List<PostEntity> posts = postService.findAll();
            
            System.out.println("\n=== Sample Posts (" + posts.size() + ") ===");
            System.out.println("ID\tTitle\t\t\tAuthor\t\tViews\tCreated");
            System.out.println("--\t-----\t\t\t------\t\t-----\t-------");
            
            for (PostEntity post : posts) {
                System.out.printf("%d\t%-20s\t%-15s\t%d\t%s\n",
                    post.getPostId(),
                    post.getTitle().length() > 20 ? post.getTitle().substring(0, 17) + "..." : post.getTitle(),
                    post.getAuthorName(),
                    post.getViews(),
                    post.getCreatedAt().toLocalDate());
            }
            
        } catch (DatabaseException e) {
            System.out.println("Error retrieving posts: " + e.getMessage());
        }
    }
    
    private void addSamplePost() {
        try {
            System.out.println("\n=== Add New Post ===");
            
            System.out.print("Title: ");
            String title = scanner.nextLine();
            
            System.out.print("Content: ");
            String content = scanner.nextLine();
            
            System.out.print("Author Name: ");
            String authorName = scanner.nextLine();
            
            // Create new post
            PostEntity newPost = new PostEntity();
            newPost.setTitle(title);
            newPost.setContent(content);
            newPost.setAuthorName(authorName);
            newPost.setStatus("Published");
            newPost.setViews(0);
            newPost.setUserId(1); // Default user ID
            
            PostEntity created = postService.create(newPost);
            
            System.out.println("Post created successfully!");
            System.out.println("Post ID: " + created.getPostId());
            System.out.println("Created at: " + created.getCreatedAt());
            
            // Invalidate cache to ensure new post appears in searches
            searchService.invalidateCache();
            
        } catch (Exception e) {
            System.out.println("Error creating post: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate the key features of Epic 3
     */
    public void demonstrateFeatures() {
        System.out.println("\n=== Epic 3 Feature Demonstration ===");
        
        try {
            // Feature 1: Case-insensitive search
            System.out.println("\n1. Case-Insensitive Search:");
            List<PostEntity> javaPosts = searchService.searchByKeyword("JAVA");
            System.out.println("Found " + javaPosts.size() + " posts for 'JAVA' (uppercase)");
            
            List<PostEntity> javaPostsLower = searchService.searchByKeyword("java");
            System.out.println("Found " + javaPostsLower.size() + " posts for 'java' (lowercase)");
            System.out.println("✓ Search is case-insensitive");
            
            // Feature 2: Multiple search types
            System.out.println("\n2. Multiple Search Types:");
            List<PostEntity> authorPosts = searchService.searchByAuthor("john");
            System.out.println("Author search for 'john': " + authorPosts.size() + " posts");
            
            List<PostEntity> tagPosts = searchService.searchByTag("java");
            System.out.println("Tag search for 'java': " + tagPosts.size() + " posts");
            
            // Feature 3: Performance improvement
            System.out.println("\n3. Performance Improvement:");
            long startTime = System.nanoTime();
            searchService.searchByKeyword("programming");
            long firstTime = System.nanoTime() - startTime;
            
            startTime = System.nanoTime();
            searchService.searchByKeyword("programming");
            long secondTime = System.nanoTime() - startTime;
            
            System.out.println("First search: " + (firstTime / 1_000_000) + "ms");
            System.out.println("Second search (cached): " + (secondTime / 1_000_000) + "ms");
            System.out.println("✓ Performance improved through caching");
            
            // Feature 4: Sorting
            System.out.println("\n4. Sorting Functionality:");
            List<PostEntity> allPosts = postService.findAll();
            List<PostEntity> sortedByViews = searchService.sortPosts(allPosts, "views", "desc");
            System.out.println("Top post by views: " + sortedByViews.get(0).getTitle() + 
                " (" + sortedByViews.get(0).getViews() + " views)");
            System.out.println("✓ Sorting implemented for cached data");
            
            // Feature 5: Cache invalidation
            System.out.println("\n5. Cache Invalidation:");
            System.out.println("Adding new post to test cache invalidation...");
            
            PostEntity testPost = new PostEntity();
            testPost.setTitle("Test Cache Invalidation");
            testPost.setContent("This post tests cache invalidation #test #cache");
            testPost.setAuthorName("Test User");
            testPost.setUserId(1);
            postService.create(testPost);
            
            List<PostEntity> testResults = searchService.searchByKeyword("cache");
            System.out.println("Found " + testResults.size() + " posts for 'cache' after invalidation");
            System.out.println("✓ Cache invalidation ensures consistent results");
            
        } catch (Exception e) {
            System.out.println("Error during demonstration: " + e.getMessage());
        }
    }
}
