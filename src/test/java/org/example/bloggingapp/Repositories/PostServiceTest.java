package org.example.bloggingapp.Repositories;

import org.example.bloggingapp.Services.PostService;
import org.example.bloggingapp.Models.PostEntity;
import org.example.bloggingapp.Utils.Exceptions.DatabaseException;
import org.example.bloggingapp.Utils.Exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PostService Tests")
class PostServiceTest {

    private PostService postService;
    private PostEntity testPost;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        postService = new PostService(null);
        testTime = LocalDateTime.now();
        testPost = new PostEntity(1, "Test Title", "Test Content", testTime, 1, "Published", 100, "Test Author");
    }

    @Test
    void create() {
        // Test null post validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> postService.create(null));
        assertEquals("POST_NULL", exception.getErrorCode());
        assertEquals("post", exception.getFieldName());
    }

    @Test
    void findById() {
        // Test invalid ID validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> postService.findById(0));
        assertEquals("INVALID_ID", exception.getErrorCode());
        assertEquals("id", exception.getFieldName());
    }

    @Test
    void findByString() {
        // Test null identifier validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> postService.findByString(null));
        assertEquals("IDENTIFIER_REQUIRED", exception.getErrorCode());
        assertEquals("identifier", exception.getFieldName());
    }

    @Test
    void findAll() {
        // Test with null repository - should throw DatabaseException
        assertThrows(DatabaseException.class, () -> postService.findAll());
    }

    @Test
    void update() {
        // Test null post validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> postService.update(1, null));
        assertEquals("POST_NULL", exception.getErrorCode());
        assertEquals("post", exception.getFieldName());
    }

    @Test
    void delete() {
        // Test invalid ID validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> postService.delete(0));
        assertEquals("INVALID_ID", exception.getErrorCode());
        assertEquals("id", exception.getFieldName());
    }

    @Nested
    @DisplayName("Additional Validation Tests")
    class AdditionalValidationTests {

        @Test
        @DisplayName("Should validate empty title in create")
        void shouldValidateEmptyTitleInCreate() {
            PostEntity postWithEmptyTitle = new PostEntity(1, "", "Test Content", testTime, 1);
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> postService.create(postWithEmptyTitle));
            assertEquals("TITLE_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("Should validate empty content in create")
        void shouldValidateEmptyContentInCreate() {
            PostEntity postWithEmptyContent = new PostEntity(1, "Test Title", "", testTime, 1);
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> postService.create(postWithEmptyContent));
            assertEquals("CONTENT_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("Should validate negative user ID")
        void shouldValidateNegativeUserId() {
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> postService.findByUserId(-1));
            assertEquals("INVALID_USER_ID", exception.getErrorCode());
            assertEquals("userId", exception.getFieldName());
        }
    }

    @Nested
    @DisplayName("Cache Management Tests")
    class CacheManagementTests {

        @Test
        @DisplayName("Should clear all caches without exceptions")
        void shouldClearAllCaches() {
            assertDoesNotThrow(() -> postService.clearAllCaches());
        }

        @Test
        @DisplayName("Should return cache stats")
        void shouldReturnCacheStats() {
            String stats = postService.getCacheStats();
            assertNotNull(stats);
            assertTrue(stats.contains("Post Cache Stats"));
        }

        @Test
        @DisplayName("Should cleanup caches without exceptions")
        void shouldCleanupCaches() {
            assertDoesNotThrow(() -> postService.cleanupCaches());
        }
    }
}