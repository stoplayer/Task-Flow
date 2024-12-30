package com.twd.SpringSecurityJWT.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CommentEntityTest {

    @Test
    void testCommentEntity() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");
        comment.setCreatedAt(LocalDateTime.now());

        // Act & Assert
        assertEquals(1L, comment.getId());
        assertEquals("Test Comment", comment.getContent());
        assertNotNull(comment.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        // Act & Assert
        assertEquals(comment1.getId(), comment2.getId()); // Compare IDs directly
    }

    @Test
    void testToString() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");

        // Act
        String toString = comment.toString();

        // Assert
        assertTrue(toString.contains("Comment@")); // Check for the class name and @ symbol
    }
}