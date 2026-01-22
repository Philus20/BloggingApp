package org.example.bloggingapp.Services;

import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PostSearchService {
    
    private final PostService postService;
    private final Map<String, List<PostEntity>> keywordCache;
    private final Map<String, List<PostEntity>> authorCache;
    private final Map<String, List<PostEntity>> tagCache;
    private final Map<Integer, PostEntity> postCache;
    private volatile LocalDateTime lastCacheUpdate;
    private final int cacheMaxSize;
    
    // Performance metrics
    private long searchCount = 0;
    private long cacheHits = 0;
    private long totalSearchTime = 0;
    
    public PostSearchService(PostService postService) {
        this.postService = postService;
        this.keywordCache = new ConcurrentHashMap<>();
        this.authorCache = new ConcurrentHashMap<>();
        this.tagCache = new ConcurrentHashMap<>();
        this.postCache = new ConcurrentHashMap<>();
        this.cacheMaxSize = 1000;
        this.lastCacheUpdate = LocalDateTime.now();
    }
    
    /**
     * Search posts by keyword (case-insensitive)
     * Searches in title and content
     */
    public List<PostEntity> searchByKeyword(String keyword) throws ValidationException, DatabaseException {
        long startTime = System.nanoTime();
        searchCount++;
        
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new ValidationException("KEYWORD_REQUIRED", "keyword", "Search keyword cannot be null or empty");
            }
            
            String normalizedKeyword = keyword.toLowerCase().trim();
            
            // Check cache first
            List<PostEntity> cachedResults = keywordCache.get(normalizedKeyword);
            if (cachedResults != null) {
                cacheHits++;
                return new ArrayList<>(cachedResults);
            }
            
            // Perform search
            List<PostEntity> allPosts = postService.findAll();
            List<PostEntity> results = allPosts.stream()
                    .filter(post -> post.getTitle() != null && post.getTitle().toLowerCase().contains(normalizedKeyword))
                    .filter(post -> post.getContent() != null && post.getContent().toLowerCase().contains(normalizedKeyword))
                    .collect(Collectors.toList());
            
            // Cache results
            cacheKeywordResults(normalizedKeyword, results);
            
            long endTime = System.nanoTime();
            totalSearchTime += (endTime - startTime);
            
            return results;
        } catch (Exception e) {
            throw new DatabaseException("SEARCH_ERROR", "Failed to search posts by keyword: " + keyword, e);
        }
    }
    
    /**
     * Search posts by author name (case-insensitive)
     */
    public List<PostEntity> searchByAuthor(String authorName) throws ValidationException, DatabaseException {
        long startTime = System.nanoTime();
        searchCount++;
        
        try {
            if (authorName == null || authorName.trim().isEmpty()) {
                throw new ValidationException("AUTHOR_REQUIRED", "authorName", "Author name cannot be null or empty");
            }
            
            String normalizedAuthor = authorName.toLowerCase().trim();
            
            // Check cache first
            List<PostEntity> cachedResults = authorCache.get(normalizedAuthor);
            if (cachedResults != null) {
                cacheHits++;
                return new ArrayList<>(cachedResults);
            }
            
            // Perform search
            List<PostEntity> allPosts = postService.findAll();
            List<PostEntity> results = allPosts.stream()
                    .filter(post -> post.getAuthorName() != null && 
                            post.getAuthorName().toLowerCase().contains(normalizedAuthor))
                    .collect(Collectors.toList());
            
            // Cache results
            cacheAuthorResults(normalizedAuthor, results);
            
            long endTime = System.nanoTime();
            totalSearchTime += (endTime - startTime);
            
            return results;
        } catch (Exception e) {
            throw new DatabaseException("SEARCH_ERROR", "Failed to search posts by author: " + authorName, e);
        }
    }
    
    /**
     * Search posts by tag (simulated - would need PostTag relationship in real implementation)
     */
    public List<PostEntity> searchByTag(String tagName) throws ValidationException, DatabaseException {
        long startTime = System.nanoTime();
        searchCount++;
        
        try {
            if (tagName == null || tagName.trim().isEmpty()) {
                throw new ValidationException("TAG_REQUIRED", "tagName", "Tag name cannot be null or empty");
            }
            
            String normalizedTag = tagName.toLowerCase().trim();
            
            // Check cache first
            List<PostEntity> cachedResults = tagCache.get(normalizedTag);
            if (cachedResults != null) {
                cacheHits++;
                return new ArrayList<>(cachedResults);
            }
            
            // For now, simulate tag search by looking for hashtags in content
            List<PostEntity> allPosts = postService.findAll();
            List<PostEntity> results = allPosts.stream()
                    .filter(post -> post.getContent() != null && 
                            post.getContent().toLowerCase().contains("#" + normalizedTag))
                    .collect(Collectors.toList());
            
            // Cache results
            cacheTagResults(normalizedTag, results);
            
            long endTime = System.nanoTime();
            totalSearchTime += (endTime - startTime);
            
            return results;
        } catch (Exception e) {
            throw new DatabaseException("SEARCH_ERROR", "Failed to search posts by tag: " + tagName, e);
        }
    }
    
    /**
     * Combined search across all fields
     */
    public List<PostEntity> searchAll(String query) throws ValidationException, DatabaseException {
        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("QUERY_REQUIRED", "query", "Search query cannot be null or empty");
        }
        
        Set<PostEntity> allResults = new HashSet<>();
        
        try {
            // Search in all fields
            allResults.addAll(searchByKeyword(query));
            allResults.addAll(searchByAuthor(query));
            allResults.addAll(searchByTag(query));
            
            // Sort by creation date (newest first)
            return allResults.stream()
                    .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DatabaseException("SEARCH_ERROR", "Failed to perform combined search: " + query, e);
        }
    }
    
    /**
     * Sort posts by different criteria
     */
    public List<PostEntity> sortPosts(List<PostEntity> posts, String sortBy, String order) {
        if (posts == null || posts.isEmpty()) {
            return posts;
        }
        
        Comparator<PostEntity> comparator;
        
        switch (sortBy.toLowerCase()) {
            case "title":
                comparator = Comparator.comparing(PostEntity::getTitle, 
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                break;
            case "views":
                comparator = Comparator.comparingInt(PostEntity::getViews);
                break;
            case "created":
                comparator = Comparator.comparing(PostEntity::getCreatedAt);
                break;
            case "author":
                comparator = Comparator.comparing(PostEntity::getAuthorName,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                break;
            default:
                comparator = Comparator.comparing(PostEntity::getCreatedAt);
        }
        
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        
        return posts.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    /**
     * Cache management methods
     */
    private void cacheKeywordResults(String keyword, List<PostEntity> results) {
        if (keywordCache.size() >= cacheMaxSize) {
            // Remove oldest entries (simple LRU simulation)
            keywordCache.clear();
        }
        keywordCache.put(keyword, new ArrayList<>(results));
        lastCacheUpdate = LocalDateTime.now();
    }
    
    private void cacheAuthorResults(String author, List<PostEntity> results) {
        if (authorCache.size() >= cacheMaxSize) {
            authorCache.clear();
        }
        authorCache.put(author, new ArrayList<>(results));
        lastCacheUpdate = LocalDateTime.now();
    }
    
    private void cacheTagResults(String tag, List<PostEntity> results) {
        if (tagCache.size() >= cacheMaxSize) {
            tagCache.clear();
        }
        tagCache.put(tag, new ArrayList<>(results));
        lastCacheUpdate = LocalDateTime.now();
    }
    
    /**
     * Invalidate cache when posts are updated
     */
    public void invalidateCache() {
        keywordCache.clear();
        authorCache.clear();
        tagCache.clear();
        postCache.clear();
        lastCacheUpdate = LocalDateTime.now();
    }
    
    /**
     * Invalidate specific cache entries
     */
    public void invalidateKeywordCache(String keyword) {
        if (keyword != null) {
            keywordCache.remove(keyword.toLowerCase());
        }
    }
    
    public void invalidateAuthorCache(String author) {
        if (author != null) {
            authorCache.remove(author.toLowerCase());
        }
    }
    
    public void invalidateTagCache(String tag) {
        if (tag != null) {
            tagCache.remove(tag.toLowerCase());
        }
    }
    
    /**
     * Performance metrics
     */
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalSearches", searchCount);
        metrics.put("cacheHits", cacheHits);
        metrics.put("cacheHitRate", searchCount > 0 ? (double) cacheHits / searchCount : 0.0);
        metrics.put("averageSearchTimeMs", searchCount > 0 ? (double) totalSearchTime / searchCount / 1_000_000 : 0.0);
        metrics.put("lastCacheUpdate", lastCacheUpdate);
        metrics.put("keywordCacheSize", keywordCache.size());
        metrics.put("authorCacheSize", authorCache.size());
        metrics.put("tagCacheSize", tagCache.size());
        return metrics;
    }
    
    /**
     * Preload cache with common searches
     */
    public void preloadCache() throws DatabaseException {
        try {
            List<PostEntity> allPosts = postService.findAll();
            
            // Pre-cache common keywords (simulate popular searches)
            String[] commonKeywords = {"java", "programming", "tutorial", "blog", "post"};
            for (String keyword : commonKeywords) {
                List<PostEntity> results = allPosts.stream()
                        .filter(post -> post.getTitle() != null && post.getTitle().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                if (!results.isEmpty()) {
                    cacheKeywordResults(keyword, results);
                }
            }
            
            // Pre-cache all authors
            Set<String> authors = allPosts.stream()
                    .map(PostEntity::getAuthorName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            for (String author : authors) {
                List<PostEntity> authorPosts = allPosts.stream()
                        .filter(post -> author.equals(post.getAuthorName()))
                        .collect(Collectors.toList());
                if (!authorPosts.isEmpty()) {
                    cacheAuthorResults(author.toLowerCase(), authorPosts);
                }
            }
            
        } catch (Exception e) {
            throw new DatabaseException("CACHE_PRELOAD_ERROR", "Failed to preload cache", e);
        }
    }
}
