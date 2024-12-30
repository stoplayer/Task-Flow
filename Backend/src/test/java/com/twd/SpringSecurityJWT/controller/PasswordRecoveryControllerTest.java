package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.service.PasswordRecoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordRecoveryControllerTest {

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @InjectMocks
    private PasswordRecoveryController passwordRecoveryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckToken_Success() {
        // Arrange
        String token = "valid-token";

        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("Token is valid");

        when(passwordRecoveryService.validateToken(token)).thenReturn(response);

        // Act
        ResponseEntity<ReqRes> result = passwordRecoveryController.checkToken(token);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getStatusCode());
        assertEquals("Token is valid", result.getBody().getMessage());
        verify(passwordRecoveryService, times(1)).validateToken(token);
    }

    @Test
    void testCheckToken_InvalidToken() {
        // Arrange
        String token = "invalid-token";

        ReqRes response = new ReqRes();
        response.setStatusCode(400);
        response.setMessage("Invalid token");

        when(passwordRecoveryService.validateToken(token)).thenReturn(response);

        // Act
        ResponseEntity<ReqRes> result = passwordRecoveryController.checkToken(token);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Expect 200 OK
        assertNotNull(result.getBody());
        assertEquals(400, result.getBody().getStatusCode());
        assertEquals("Invalid token", result.getBody().getMessage());
        verify(passwordRecoveryService, times(1)).validateToken(token);
    }
}