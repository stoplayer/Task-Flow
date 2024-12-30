package com.twd.SpringSecurityJWT.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenEntityTest {

    @Test
    void testPasswordResetTokenEntity() {
        // Arrange
        OurUsers user = new OurUsers();
        user.setId(1);
        user.setEmail("test@example.com");

        PasswordResetToken token = new PasswordResetToken();
        token.setId(1L);
        token.setToken("test-token");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        // Act & Assert
        assertEquals(1L, token.getId());
        assertEquals("test-token", token.getToken());
        assertEquals(user, token.getUser());
        assertNotNull(token.getCreatedAt());
        assertNotNull(token.getExpiryDate());
    }

    @Test
    void testDefaultConstructor() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken();

        // Act & Assert
        assertNotNull(token.getCreatedAt()); // createdAt should be set automatically
    }

    @Test
    void testIsExpired_NotExpired() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiryDate(LocalDateTime.now().plusHours(1)); // Set expiry date in the future

        // Act
        boolean isExpired = token.isExpired();

        // Assert
        assertFalse(isExpired); // Token should not be expired
    }

    @Test
    void testIsExpired_Expired() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiryDate(LocalDateTime.now().minusHours(1)); // Set expiry date in the past

        // Act
        boolean isExpired = token.isExpired();

        // Assert
        assertTrue(isExpired); // Token should be expired
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        OurUsers user = new OurUsers();
        user.setId(1);

        PasswordResetToken token1 = new PasswordResetToken();
        token1.setId(1L);
        token1.setToken("test-token");
        token1.setUser(user);

        PasswordResetToken token2 = new PasswordResetToken();
        token2.setId(1L);
        token2.setToken("test-token");
        token2.setUser(user);

        // Act & Assert
        assertEquals(token1, token2); // Should be equal
        assertEquals(token1.hashCode(), token2.hashCode()); // Hash codes should match
    }

    @Test
    void testNotEquals() {
        // Arrange
        OurUsers user1 = new OurUsers();
        user1.setId(1);

        OurUsers user2 = new OurUsers();
        user2.setId(2);

        PasswordResetToken token1 = new PasswordResetToken();
        token1.setId(1L);
        token1.setToken("test-token-1");
        token1.setUser(user1);

        PasswordResetToken token2 = new PasswordResetToken();
        token2.setId(2L);
        token2.setToken("test-token-2");
        token2.setUser(user2);

        // Act & Assert
        assertNotEquals(token1, token2); // Should not be equal
        assertNotEquals(token1.hashCode(), token2.hashCode()); // Hash codes should not match
    }

    @Test
    void testToString() {
        // Arrange
        OurUsers user = new OurUsers();
        user.setId(1);

        PasswordResetToken token = new PasswordResetToken();
        token.setId(1L);
        token.setToken("test-token");
        token.setUser(user);

        // Act
        String toString = token.toString();

        // Assert
        assertTrue(toString.contains("test-token")); // Should include the token
        assertTrue(toString.contains("1")); // Should include the ID
    }
}