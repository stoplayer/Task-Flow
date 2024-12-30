package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OurUserDetailsServicetest {

    @Mock
    private OurUserRepo ourUserRepo;

    @InjectMocks
    private OurUserDetailsService ourUserDetailsService;

    private OurUsers testUser;
    private String testEmail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testEmail = "testuser@example.com";
        testUser = new OurUsers();
        testUser.setEmail(testEmail);
        testUser.setProfilePicture(new byte[]{1, 2, 3}, "image/jpeg", "profile.jpg");
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        // Mock the repository method to return the user
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Test the method
        var userDetails = ourUserDetailsService.loadUserByUsername(testEmail);

        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername());

        // Verify that the repository's findByEmail method was called once
        verify(ourUserRepo, times(1)).findByEmail(testEmail);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Mock the repository method to return empty
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Test that the UsernameNotFoundException is thrown
        assertThrows(UsernameNotFoundException.class, () ->
                ourUserDetailsService.loadUserByUsername(testEmail)
        );

        // Verify the repository method was called once
        verify(ourUserRepo, times(1)).findByEmail(testEmail);
    }

    @Test
    void testGetAllUsers() {
        // Test the method to get all users
        when(ourUserRepo.findAll()).thenReturn(List.of(testUser));

        var users = ourUserDetailsService.getAllUsers();

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals(testUser, users.get(0));

        // Verify that findAll was called once
        verify(ourUserRepo, times(1)).findAll();
    }

    @Test
    void testUpdateProfilePicture() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn(new byte[]{4, 5, 6});
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getOriginalFilename()).thenReturn("newProfilePic.png");

        // Mock the repository to return the user
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Perform the profile picture update
        ourUserDetailsService.updateProfilePicture(testEmail, mockFile);

        assertArrayEquals(new byte[]{4, 5, 6}, testUser.getProfilePicture());
        assertEquals("image/png", testUser.getProfilePictureType());
        assertEquals("newProfilePic.png", testUser.getProfilePictureName());

        // Verify the repository's save method was called once
        verify(ourUserRepo, times(1)).save(testUser);
    }

    @Test
    void testGetProfilePicture() {
        // Mock the repository to return the user with profile picture
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Test the method
        byte[] profilePic = ourUserDetailsService.getProfilePicture(testEmail);

        assertNotNull(profilePic);
        assertArrayEquals(new byte[]{1, 2, 3}, profilePic);

        // Verify the repository method was called once
        verify(ourUserRepo, times(1)).findByEmail(testEmail);
    }

    @Test
    void testGetProfilePictureType() {
        // Mock the repository to return the user with profile picture type
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Test the method
        String profilePicType = ourUserDetailsService.getProfilePictureType(testEmail);

        assertNotNull(profilePicType);
        assertEquals("image/jpeg", profilePicType);

        // Verify the repository method was called once
        verify(ourUserRepo, times(1)).findByEmail(testEmail);
    }

    @Test
    void testDeleteProfilePicture() {
        // Mock the repository to return the user with a profile picture
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Perform the delete profile picture operation
        ourUserDetailsService.deleteProfilePicture(testEmail);

        assertNull(testUser.getProfilePicture());
        assertNull(testUser.getProfilePictureType());
        assertNull(testUser.getProfilePictureName());

        // Verify the repository's save method was called once
        verify(ourUserRepo, times(1)).save(testUser);
    }

    @Test
    void testHasProfilePicture_UserHasProfilePicture() {
        // Mock the repository to return the user with a profile picture
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Test that the user has a profile picture
        boolean hasProfilePic = ourUserDetailsService.hasProfilePicture(testEmail);

        assertTrue(hasProfilePic);

        // Verify the repository method was called once
        verify(ourUserRepo, times(1)).findByEmail(testEmail);
    }

    @Test
    void testHasProfilePicture_UserDoesNotHaveProfilePicture() {
        // Mock the repository to return the user without a profile picture
        testUser.setProfilePicture(null, null, null);
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Test that the user doesn't have a profile picture
        boolean hasProfilePic = ourUserDetailsService.hasProfilePicture(testEmail);

        assertFalse(hasProfilePic);

        // Verify the repository method was called once
        verify(ourUserRepo, times(1)).findByEmail(testEmail);
    }

    @Test
    void testGetUserByEmail() {
        // Mock the repository to return the user
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Test the method
        OurUsers user = ourUserDetailsService.getUserByEmail(testEmail);

        assertNotNull(user);
        assertEquals(testEmail, user.getEmail());

        // Verify the repository method was called once
        verify(ourUserRepo, times(1)).findByEmail(testEmail);
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        // Mock the repository to return empty
        when(ourUserRepo.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Test that the UsernameNotFoundException is thrown
        assertThrows(UsernameNotFoundException.class, () ->
                ourUserDetailsService.getUserByEmail(testEmail)
        );

        // Verify the repository method was called once
        verify(ourUserRepo, times(1)).findByEmail(testEmail);
    }
}
