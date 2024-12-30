package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp() {
        // Arrange
        ReqRes signUpRequest = new ReqRes();
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password");

        ReqRes signUpResponse = new ReqRes();
        signUpResponse.setStatusCode(200);
        signUpResponse.setMessage("User registered successfully");

        when(authService.signUp(signUpRequest)).thenReturn(signUpResponse);

        // Act
        ResponseEntity<ReqRes> response = authController.signUp(signUpRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("User registered successfully", response.getBody().getMessage());
        verify(authService, times(1)).signUp(signUpRequest);
    }

    @Test
    void testSignIn() {
        // Arrange
        ReqRes signInRequest = new ReqRes();
        signInRequest.setEmail("test@example.com");
        signInRequest.setPassword("password");

        ReqRes signInResponse = new ReqRes();
        signInResponse.setStatusCode(200);
        signInResponse.setMessage("User signed in successfully");
        signInResponse.setToken("jwt-token");

        when(authService.signIn(signInRequest)).thenReturn(signInResponse);

        // Act
        ResponseEntity<ReqRes> response = authController.signIn(signInRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("User signed in successfully", response.getBody().getMessage());
        assertEquals("jwt-token", response.getBody().getToken());
        verify(authService, times(1)).signIn(signInRequest);
    }

    @Test
    void testRefreshToken() {
        // Arrange
        ReqRes refreshTokenRequest = new ReqRes();
        refreshTokenRequest.setToken("old-jwt-token");

        ReqRes refreshTokenResponse = new ReqRes();
        refreshTokenResponse.setStatusCode(200);
        refreshTokenResponse.setMessage("Token refreshed successfully");
        refreshTokenResponse.setToken("new-jwt-token");

        when(authService.refreshToken(refreshTokenRequest)).thenReturn(refreshTokenResponse);

        // Act
        ResponseEntity<ReqRes> response = authController.refreshToken(refreshTokenRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Token refreshed successfully", response.getBody().getMessage());
        assertEquals("new-jwt-token", response.getBody().getToken());
        verify(authService, times(1)).refreshToken(refreshTokenRequest);
    }
}