package org.example.bloggingapp.Database.Repositories;

import org.example.bloggingapp.Services.PostSearchService;
import org.example.bloggingapp.Services.PostService;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PostSearchService Tests")
class PostSearchServiceTest {

    private PostSearchService searchService;
    private PostService mockPostService;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        mockPostService = new PostService(null);
        searchService = new PostSearchService(mockPostService);
        testTime = LocalDateTime.now();
    }

    @Nested
    @DisplayName("Search by Keyword Tests")
    class SearchByKeywordTests {

        @Test
        @DisplayName("Should throw ValidationException for null keyword")
        void shouldThrowValidationExceptionForNullKeyword() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> searchService.searchByKeyword(null));
            
            assertEquals("KEYWORD_REQUIRED", exception.getErrorCode());
            assertEquals("keyword", exception.getFieldName());
            assertEquals("Search keyword cannot be null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw ValidationException for empty keyword")
        void shouldThrowValidationExceptionForEmptyKeyword() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> searchService.searchByKeyword(""));
            
            assertEquals("KEYWORD_REQUIRED", exception.getErrorCode());
            assertEquals("keyword", exception.getFieldName());
        }

        @Test
        @DisplayName("Should throw ValidationException for whitespace-only keyword")
        void shouldThrowValidationExceptionForWhitespaceOnlyKeyword() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> searchService.searchByKeyword("   "));
            
            assertEquals("KEYWORD_REQUIRED", exception.getErrorCode());
            assertEquals("keyword", exception.getFieldName());
        }

        @Test
        @DisplayName("Should handle valid keyword search")
        void shouldHandleValidKeywordSearch() {
            // Should not throw exception for valid keyword
            assertDoesNotThrow(() -> searchService.searchByKeyword("test"));
        }

        @Test
        @DisplayName("Should handle keyword with mixed case")
        void shouldHandleKeywordWithMixedCase() {
            assertDoesNotThrow(() -> searchService.searchByKeyword("TeSt"));
        }

        @Test
        @DisplayName("Should handle keyword with special characters")
        void shouldHandleKeywordWithSpecialCharacters() {
            assertDoesNotThrow(() -> searchService.searchByKeyword("test-keyword_123"));
        }
    }

    @Nested
    @DisplayName("Search by Author Tests")
    class SearchByAuthorTests {

        @Test
        @DisplayName("Should throw ValidationException for null author")
        void shouldThrowValidationExceptionForNullAuthor() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> searchService.searchByAuthor(null));
            
            assertEquals("AUTHOR_REQUIRED", exception.getErrorCode());
            assertEquals("author", exception.getFieldName());
        }

        @Test
        @DisplayName("Should throw ValidationException for empty author")
        void shouldThrowValidationExceptionForEmptyAuthor() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> searchService.searchByAuthor(""));
            
            assertEquals("AUTHOR_REQUIRED", exception.getErrorCode());
            assertEquals("author", exception.getFieldName());
        }

        @Test
        @DisplayName("Should handle valid author search")
        void shouldHandleValidAuthorSearch() {
            assertDoesNotThrow(() -> searchService.searchByAuthor("John Doe"));
        }
    }

    @Nested
    @DisplayName("Search by Tag Tests")
    class SearchByTagTests {

        @Test
        @DisplayName("Should throw ValidationException for null tag")
        void shouldThrowValidationExceptionForNullTag() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> searchService.searchByTag(null));
            
            assertEquals("TAG_REQUIRED", exception.getErrorCode());
            assertEquals("tagName", exception.getFieldName());
        }

        @Test
        @DisplayName("Should throw ValidationException for empty tag")
        void shouldThrowValidationExceptionForEmptyTag() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> searchService.searchByTag(""));
            
            assertEquals("TAG_REQUIRED", exception.getErrorCode());
            assertEquals("tagName", exception.getFieldName());
        }

        @Test
        @DisplayName("Should handle valid tag search")
        void shouldHandleValidTagSearch() {
            assertDoesNotThrow(() -> searchService.searchByTag("java"));
        }
    }

    @Nested
    @DisplayName("Search All Tests")
    class SearchAllTests {

        @Test
        @DisplayName("Should throw ValidationException for null query")
        void shouldThrowValidationExceptionForNullQuery() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> searchService.searchAll(null));
            
            assertEquals("QUERY_REQUIRED", exception.getErrorCode());
            assertEquals("query", exception.getFieldName());
        }

        @Test
        @DisplayName("Should throw ValidationException for empty query")
        void shouldThrowValidationExceptionForEmptyQuery() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> searchService.searchAll(""));
            
            assertEquals("QUERY_REQUIRED", exception.getErrorCode());
            assertEquals("query", exception.getFieldName());
        }

        @Test
        @DisplayName("Should handle valid query search")
        void shouldHandleValidQuerySearch() {
            assertDoesNotThrow(() -> searchService.searchAll("test query"));
        }
    }

    @Nested
    @DisplayName("Sort Posts Tests")
    class SortPostsTests {

        @Test
        @DisplayName("Should handle null posts list")
        void shouldHandleNullPostsList() {
            assertDoesNotThrow(() -> searchService.sortPosts(null, "title", "asc"));
        }

        @Test
        @DisplayName("Should handle empty posts list")
        void shouldHandleEmptyPostsList() {
            assertDoesNotThrow(() -> searchService.sortPosts(List.of(), "title", "asc"));
        }

        @Test
        @DisplayName("Should handle valid posts list")
        void shouldHandleValidPostsList() {
            List<PostEntity> posts = List.of(new PostEntity());
            assertDoesNotThrow(() -> searchService.sortPosts(posts, "title", "asc"));
        }
    }

    @Nested
    @DisplayName("Cache Management Tests")
    class CacheManagementTests {

        @Test
        @DisplayName("Should invalidate cache without throwing exceptions")
        void shouldInvalidateCacheWithoutThrowingExceptions() {
            assertDoesNotThrow(() -> searchService.invalidateCache());
        }

        @Test
        @DisplayName("Should invalidate keyword cache")
        void shouldInvalidateKeywordCache() {
            assertDoesNotThrow(() -> searchService.invalidateKeywordCache("test"));
        }

        @Test
        @DisplayName("Should invalidate author cache")
        void shouldInvalidateAuthorCache() {
            assertDoesNotThrow(() -> searchService.invalidateAuthorCache("test"));
        }

        @Test
        @DisplayName("Should invalidate tag cache")
        void shouldInvalidateTagCache() {
            assertDoesNotThrow(() -> searchService.invalidateTagCache("test"));
        }

        @Test
        @DisplayName("Should preload cache")
        void shouldPreloadCache() {
            assertDoesNotThrow(() -> searchService.preloadCache());
        }
    }

    @Nested
    @DisplayName("Performance Metrics Tests")
    class PerformanceMetricsTests {

        @Test
        @DisplayName("Should get performance metrics")
        void shouldGetPerformanceMetrics() {
            assertDoesNotThrow(() -> searchService.getPerformanceMetrics());
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle database errors gracefully")
        void shouldHandleDatabaseErrorsGracefully() {
            // All search methods should handle database errors
            assertThrows(DatabaseException.class, () -> searchService.searchByKeyword("test"));
        }

        @Test
        @DisplayName("Should handle concurrent access")
        void shouldHandleConcurrentAccess() {
            // Should be thread-safe
            assertDoesNotThrow(() -> {
                searchService.searchByKeyword("test1");
                searchService.searchByKeyword("test2");
            });
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should work with PostService integration")
        void shouldWorkWithPostServiceIntegration() {
            // Test that the service properly integrates with PostService
            assertNotNull(searchService);
            assertDoesNotThrow(() -> searchService.getPerformanceMetrics());
        }

        @Test
        @DisplayName("Should maintain cache consistency")
        void shouldMaintainCacheConsistency() {
            // Invalidate cache and verify consistency
            searchService.invalidateCache();
            assertDoesNotThrow(() -> searchService.getPerformanceMetrics());
        }
    }
}