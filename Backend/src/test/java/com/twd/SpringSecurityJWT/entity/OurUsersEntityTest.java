package com.twd.SpringSecurityJWT.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OurUsersEntityTest {

    @Test
    void testOurUsersEntity() {
        // Arrange
        OurUsers user = new OurUsers();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole("ROLE_USER");

        // Act & Assert
        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("ROLE_USER", user.getRole());
    }

    @Test
    void testUserDetailsMethods() {
        // Arrange
        OurUsers user = new OurUsers();
        user.setEmail("john.doe@example.com");
        user.setRole("ROLE_USER");

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String username = user.getUsername();
        boolean isAccountNonExpired = user.isAccountNonExpired();
        boolean isAccountNonLocked = user.isAccountNonLocked();
        boolean isCredentialsNonExpired = user.isCredentialsNonExpired();
        boolean isEnabled = user.isEnabled();

        // Assert
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals("john.doe@example.com", username);
        assertTrue(isAccountNonExpired);
        assertTrue(isAccountNonLocked);
        assertTrue(isCredentialsNonExpired);
        assertTrue(isEnabled);
    }

    @Test
    void testPrePersist() {
        // Arrange
        OurUsers user = new OurUsers();

        // Act
        user.onCreate(); // Simulate @PrePersist

        // Assert
        assertNotNull(user.getDateInscription()); // dateInscription should be set
    }

    @Test
    void testProfilePictureMethods() {
        // Arrange
        OurUsers user = new OurUsers();
        byte[] profilePicture = new byte[]{1, 2, 3};
        String contentType = "image/jpeg";
        String fileName = "profile.jpg";

        // Act
        user.setProfilePicture(profilePicture, contentType, fileName);

        // Assert
        assertArrayEquals(profilePicture, user.getProfilePicture());
        assertEquals(contentType, user.getProfilePictureType());
        assertEquals(fileName, user.getProfilePictureName());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        OurUsers user1 = new OurUsers();
        user1.setId(1);
        user1.setEmail("john.doe@example.com");

        OurUsers user2 = new OurUsers();
        user2.setId(1);
        user2.setEmail("john.doe@example.com");

        // Act & Assert
        assertEquals(user1, user2); // Should be equal
        assertEquals(user1.hashCode(), user2.hashCode()); // Hash codes should match
    }

    @Test
    void testNotEquals() {
        // Arrange
        OurUsers user1 = new OurUsers();
        user1.setId(1);
        user1.setEmail("john.doe@example.com");

        OurUsers user2 = new OurUsers();
        user2.setId(2);
        user2.setEmail("jane.doe@example.com");

        // Act & Assert
        assertNotEquals(user1, user2); // Should not be equal
        assertNotEquals(user1.hashCode(), user2.hashCode()); // Hash codes should not match
    }

    @Test
    void testToString() {
        // Arrange
        OurUsers user = new OurUsers();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        // Act
        String toString = user.toString();

        // Assert
        assertTrue(toString.contains("John Doe")); // Should include the name
        assertTrue(toString.contains("john.doe@example.com")); // Should include the email
    }
}