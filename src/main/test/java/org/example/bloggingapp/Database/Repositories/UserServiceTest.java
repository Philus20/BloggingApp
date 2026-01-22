package org.example.bloggingapp.Database.Repositories;

import org.example.bloggingapp.Services.UserService;
import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserService Tests")
class UserServiceTest {

    private UserService userService;
    private UserEntity testUser;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        userService = new UserService(null);
        testTime = LocalDateTime.now();
        testUser = new UserEntity("testuser", "test@example.com", "password123", "USER", testTime);
        testUser.setUserId(1);
    }

    @Test
    void create() {
        // Test null user validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> userService.create(null));
        assertEquals("USER_NULL", exception.getErrorCode());
        assertEquals("user", exception.getFieldName());
    }

    @Test
    void findById() {
        // Test invalid ID validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> userService.findById(0));
        assertEquals("INVALID_ID", exception.getErrorCode());
        assertEquals("id", exception.getFieldName());
    }

    @Test
    void findByString() {
        // Test null identifier validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> userService.findByString(null));
        assertEquals("IDENTIFIER_REQUIRED", exception.getErrorCode());
        assertEquals("identifier", exception.getFieldName());
    }

    @Test
    void findAll() {
        // Test with null repository - should throw DatabaseException
        assertThrows(DatabaseException.class, () -> userService.findAll());
    }

    @Test
    void update() {
        // Test null user validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> userService.update(1, null));
        assertEquals("USER_NULL", exception.getErrorCode());
        assertEquals("user", exception.getFieldName());
    }

    @Test
    void delete() {
        // Test invalid ID validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> userService.delete(0));
        assertEquals("INVALID_ID", exception.getErrorCode());
        assertEquals("id", exception.getFieldName());
    }

    @Nested
    @DisplayName("Additional Validation Tests")
    class AdditionalValidationTests {

        @Test
        @DisplayName("Should validate empty email in create")
        void shouldValidateEmptyEmailInCreate() {
            UserEntity userWithEmptyEmail = new UserEntity("testuser", "", "password123", "USER", testTime);
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> userService.create(userWithEmptyEmail));
            assertEquals("EMAIL_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("Should validate empty username in create")
        void shouldValidateEmptyUsernameInCreate() {
            UserEntity userWithEmptyUsername = new UserEntity("", "test@example.com", "password123", "USER", testTime);
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> userService.create(userWithEmptyUsername));
            assertEquals("USERNAME_REQUIRED", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Cache Management Tests")
    class CacheManagementTests {

        @Test
        @DisplayName("Should clear all caches without exceptions")
        void shouldClearAllCaches() {
            assertDoesNotThrow(() -> userService.clearAllCaches());
        }

        @Test
        @DisplayName("Should return cache stats")
        void shouldReturnCacheStats() {
            String stats = userService.getCacheStats();
            assertNotNull(stats);
            assertTrue(stats.contains("User Cache Stats"));
        }

        @Test
        @DisplayName("Should cleanup caches without exceptions")
        void shouldCleanupCaches() {
            assertDoesNotThrow(() -> userService.cleanupCaches());
        }
    }
}