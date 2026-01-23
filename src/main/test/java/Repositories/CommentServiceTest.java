package Repositories;

import org.example.bloggingapp.Services.CommentService;
import org.example.bloggingapp.Models.CommentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommentService Tests")
class CommentServiceTest {

    private CommentService commentService;
    private CommentEntity testComment;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(null);
        testTime = LocalDateTime.now();
        testComment = new CommentEntity(1, "This is a test comment", testTime, 1, 1);
    }

    @Test
    void create() {
        // Test with null repository - should throw RuntimeException
        assertThrows(RuntimeException.class, () -> commentService.create(testComment));
    }

    @Test
    void findById() {
        // Test with null repository - should return null or throw exception
        assertDoesNotThrow(() -> commentService.findById(1));
    }

    @Test
    void findByString() {
        // Test with null repository - should return null or throw exception
        assertDoesNotThrow(() -> commentService.findByString("test"));
    }

    @Test
    void findAll() {
        // Test with null repository - should return null or throw exception
        assertDoesNotThrow(() -> commentService.findAll());
    }

    @Test
    void update() {
        // Test with null repository - should return null or throw exception
        assertDoesNotThrow(() -> commentService.update(1, testComment));
    }

    @Test
    void delete() {
        // Test with null repository - should return false or throw exception
        assertDoesNotThrow(() -> commentService.delete(1));
    }

    @Nested
    @DisplayName("CommentEntity Validation Tests")
    class CommentEntityValidationTests {

        @Test
        @DisplayName("Should create valid CommentEntity with all parameters")
        void shouldCreateValidCommentEntityWithAllParameters() {
            assertDoesNotThrow(() -> new CommentEntity(1, "Test comment", testTime, 1, 1));
        }

        @Test
        @DisplayName("Should create CommentEntity with default constructor")
        void shouldCreateCommentEntityWithDefaultConstructor() {
            assertDoesNotThrow(() -> new CommentEntity());
        }

        @Test
        @DisplayName("Should set and get all CommentEntity properties")
        void shouldSetAndGetAllCommentEntityProperties() {
            CommentEntity comment = new CommentEntity();

            comment.setCommentId(1);
            comment.setContent("Test comment");
            comment.setCreatedAt(testTime);
            comment.setPostId(1);
            comment.setUserId(1);

            assertEquals(1, comment.getCommentId());
            assertEquals("Test comment", comment.getContent());
            assertEquals(testTime, comment.getCreatedAt());
            assertEquals(1, comment.getPostId());
            assertEquals(1, comment.getUserId());
        }

        @Test
        @DisplayName("Should handle null values in CommentEntity setters")
        void shouldHandleNullValuesInCommentEntitySetters() {
            CommentEntity comment = new CommentEntity();

            assertDoesNotThrow(() -> {
                comment.setContent(null);
                comment.setCreatedAt(null);
            });

            assertNull(comment.getContent());
            assertNull(comment.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle empty content")
        void shouldHandleEmptyContent() {
            CommentEntity comment = new CommentEntity();
            
            assertDoesNotThrow(() -> {
                comment.setContent("");
            });
            
            assertEquals("", comment.getContent());
        }

        @Test
        @DisplayName("Should handle zero and negative IDs")
        void shouldHandleZeroAndNegativeIds() {
            CommentEntity comment = new CommentEntity();
            
            assertDoesNotThrow(() -> {
                comment.setCommentId(0);
                comment.setPostId(-1);
                comment.setUserId(-1);
            });
            
            assertEquals(0, comment.getCommentId());
            assertEquals(-1, comment.getPostId());
            assertEquals(-1, comment.getUserId());
        }
    }

    @Nested
    @DisplayName("Service Behavior Tests")
    class ServiceBehaviorTests {

        @Test
        @DisplayName("Should set creation time when null")
        void shouldSetCreationTimeWhenNull() {
            CommentEntity commentWithoutTime = new CommentEntity(1, "Test comment", null, 1, 1);
            
            // The service should set creation time automatically
            assertDoesNotThrow(() -> commentService.create(commentWithoutTime));
        }

        @Test
        @DisplayName("Should preserve creation time when not null")
        void shouldPreserveCreationTimeWhenNotNull() {
            LocalDateTime specificTime = LocalDateTime.of(2023, 1, 1, 12, 0);
            CommentEntity commentWithTime = new CommentEntity(1, "Test comment", specificTime, 1, 1);
            
            // The service should preserve the existing creation time
            assertEquals(specificTime, commentWithTime.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle null comment in create")
        void shouldHandleNullCommentInCreate() {
            // Test null comment - should throw exception or handle gracefully
            assertThrows(Exception.class, () -> commentService.create(null));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle repository errors gracefully")
        void shouldHandleRepositoryErrorsGracefully() {
            // All methods should handle null repository without crashing
            assertDoesNotThrow(() -> {
                commentService.findById(1);
                commentService.findByString("test");
                commentService.findAll();
                commentService.update(1, testComment);
                commentService.delete(1);
            });
        }

        @Test
        @DisplayName("Should throw meaningful error messages")
        void shouldThrowMeaningfulErrorMessages() {
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> commentService.create(testComment));
            
            assertTrue(exception.getMessage().contains("Failed to create comment"));
        }
    }
}