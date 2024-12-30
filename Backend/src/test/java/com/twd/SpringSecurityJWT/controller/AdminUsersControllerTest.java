package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.Product;
import com.twd.SpringSecurityJWT.repository.ProductRepo;
import com.twd.SpringSecurityJWT.service.OurUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUsersControllerTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private OurUserDetailsService ourUserDetailsService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminUsers adminUsersController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setName("Product 1");
        Product product2 = new Product();
        product2.setName("Product 2");
        when(productRepo.findAll()).thenReturn(Arrays.asList(product1, product2));

        // Act
        ResponseEntity<Object> response = adminUsersController.getAllProducts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    @Test
    void testSignUp() {
        // Arrange
        ReqRes productRequest = new ReqRes();
        productRequest.setName("New Product");
        Product productToSave = new Product();
        productToSave.setName(productRequest.getName());
        when(productRepo.save(any(Product.class))).thenReturn(productToSave);

        // Act
        ResponseEntity<Object> response = adminUsersController.signUp(productRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Product);
        assertEquals("New Product", ((Product) response.getBody()).getName());
    }

    @Test
    void testUserAlone() {
        // Act
        ResponseEntity<Object> response = adminUsersController.userAlone();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Users alone can access this API only", response.getBody());
    }

    @Test
    void testBothAdminAndUsersApi() {
        // Act
        ResponseEntity<Object> response = adminUsersController.bothAdminAndUsersApi();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Both Admin and Users Can access the api", response.getBody());
    }

    @Test
    void testGetCurrentUserEmail() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");

        // Act
        String email = adminUsersController.getCurrentUserEmail();

        // Assert
        assertEquals("user@example.com", email);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        OurUsers user1 = new OurUsers();
        user1.setEmail("user1@example.com");
        OurUsers user2 = new OurUsers();
        user2.setEmail("user2@example.com");
        when(ourUserDetailsService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Act
        ResponseEntity<List<OurUsers>> response = adminUsersController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testUploadProfilePicture_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");

        // Act
        ResponseEntity<Map<String, String>> response = adminUsersController.uploadProfilePicture(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profile picture uploaded successfully.", response.getBody().get("message"));
    }

    @Test
    void testUploadProfilePicture_InvalidFileType() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "invalid content".getBytes());

        // Act
        ResponseEntity<Map<String, String>> response = adminUsersController.uploadProfilePicture(file);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Only image files (e.g., jpg, png) are allowed.", response.getBody().get("error"));
    }

    @Test
    void testGetProfilePicture_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        when(ourUserDetailsService.hasProfilePicture("user@example.com")).thenReturn(true);
        when(ourUserDetailsService.getProfilePicture("user@example.com")).thenReturn("image data".getBytes());
        when(ourUserDetailsService.getProfilePictureType("user@example.com")).thenReturn(MediaType.IMAGE_JPEG_VALUE);

        // Act
        ResponseEntity<byte[]> response = adminUsersController.getProfilePicture();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG_VALUE, response.getHeaders().getContentType().toString());
        assertNotNull(response.getBody());
    }

    @Test
    void testDeleteProfilePicture_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        when(ourUserDetailsService.hasProfilePicture("user@example.com")).thenReturn(true);

        // Act
        ResponseEntity<Map<String, String>> response = adminUsersController.deleteProfilePicture();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profile picture deleted successfully.", response.getBody().get("message"));
    }

    @Test
    void testGetUserProfilePicture_Success() {
        // Arrange
        when(ourUserDetailsService.hasProfilePicture("user@example.com")).thenReturn(true);
        when(ourUserDetailsService.getProfilePicture("user@example.com")).thenReturn("image data".getBytes());
        when(ourUserDetailsService.getProfilePictureType("user@example.com")).thenReturn(MediaType.IMAGE_JPEG_VALUE);

        // Act
        ResponseEntity<byte[]> response = adminUsersController.getUserProfilePicture("user@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG_VALUE, response.getHeaders().getContentType().toString());
        assertNotNull(response.getBody());
    }
}