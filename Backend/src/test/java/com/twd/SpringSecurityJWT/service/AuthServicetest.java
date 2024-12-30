package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServicetest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private OurUserRepo ourUserRepo;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp_Successful() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setEmail("diaeino@gmail.com");
        reqRes.setPassword("Diae@2002");
        reqRes.setName("Test User");
        reqRes.setRole("USER");

        OurUsers savedUser = new OurUsers();
        savedUser.setId(1);
        savedUser.setEmail("diaeino@gmail.com");
        savedUser.setName("Test User");
        savedUser.setRole("USER");

        when(ourUserRepo.findByEmail(reqRes.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(reqRes.getPassword())).thenReturn("encodedPassword");
        when(ourUserRepo.save(any(OurUsers.class))).thenReturn(savedUser);

        // Act
        ReqRes response = authService.signUp(reqRes);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("User Saved Successfully", response.getMessage());
        assertNotNull(response.getOurUsers());
        verify(ourUserRepo).save(any(OurUsers.class));
    }

    @Test
    void testSignUp_EmailAlreadyExists() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setEmail("diaeino@gmail.com");

        OurUsers existingUser = new OurUsers();
        existingUser.setEmail("diaeino@gmail.com");

        when(ourUserRepo.findByEmail(reqRes.getEmail())).thenReturn(Optional.of(existingUser));

        // Act
        ReqRes response = authService.signUp(reqRes);

        // Assert
        assertEquals(500, response.getStatusCode());
        assertEquals("Email already exists", response.getError());
        verify(ourUserRepo, never()).save(any(OurUsers.class));
    }

    @Test
    void testSignUp_InvalidEmailFormat() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setEmail("invalid-email");
        reqRes.setPassword("Diae@2002");
        reqRes.setName("Test User");
        reqRes.setRole("USER");

        // Act
        ReqRes response = authService.signUp(reqRes);

        // Assert
        assertEquals(500, response.getStatusCode());
        assertEquals("Invalid email format", response.getError());
        verify(ourUserRepo, never()).save(any(OurUsers.class));
    }

    @Test
    void testSignUp_InvalidPasswordFormat() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setEmail("diaeino@gmail.com");
        reqRes.setPassword("weak");
        reqRes.setName("Test User");
        reqRes.setRole("USER");

        // Act
        ReqRes response = authService.signUp(reqRes);

        // Assert
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getError().contains("Password must be at least 8 characters long"));
        verify(ourUserRepo, never()).save(any(OurUsers.class));
    }

    @Test
    void testSignUp_NullRole_DefaultsToUser() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setEmail("diaeino@gmail.com");
        reqRes.setPassword("Diae@2002");
        reqRes.setName("Test User");
        reqRes.setRole(null); // Null role

        OurUsers savedUser = new OurUsers();
        savedUser.setId(1);
        savedUser.setEmail("diaeino@gmail.com");
        savedUser.setName("Test User");
        savedUser.setRole("USER"); // Default role

        when(ourUserRepo.findByEmail(reqRes.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(reqRes.getPassword())).thenReturn("encodedPassword");
        when(ourUserRepo.save(any(OurUsers.class))).thenReturn(savedUser);

        // Act
        ReqRes response = authService.signUp(reqRes);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("User Saved Successfully", response.getMessage());
        assertEquals("USER", response.getOurUsers().getRole()); // Verify default role
        verify(ourUserRepo).save(any(OurUsers.class));
    }

    @Test
    void testSignIn_Successful() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setEmail("diaeino@gmail.com");
        reqRes.setPassword("Diae@2002");

        OurUsers user = new OurUsers();
        user.setId(1);
        user.setEmail("diaeino@gmail.com");
        user.setName("Test User");
        user.setRole("USER");

        when(ourUserRepo.findByEmail(reqRes.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn("mockToken");
        when(jwtUtils.generateRefreshToken(any(HashMap.class), eq(user))).thenReturn("mockRefreshToken");

        // Act
        ReqRes response = authService.signIn(reqRes);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("Successfully Signed In", response.getMessage());
        assertEquals("mockToken", response.getToken());
        assertEquals("mockRefreshToken", response.getRefreshToken());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void testSignIn_UserNotFound() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setEmail("unknown@example.com");

        when(ourUserRepo.findByEmail(reqRes.getEmail())).thenReturn(Optional.empty());

        // Act
        ReqRes response = authService.signIn(reqRes);

        // Assert
        assertEquals(500, response.getStatusCode());
        assertEquals("User not found", response.getError());
    }

    @Test
    void testRefreshToken_Successful() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setToken("validToken");

        OurUsers user = new OurUsers();
        user.setEmail("diaeino@gmail.com");

        when(jwtUtils.extractUsername(reqRes.getToken())).thenReturn("diaeino@gmail.com");
        when(ourUserRepo.findByEmail("diaeino@gmail.com")).thenReturn(Optional.of(user));
        when(jwtUtils.isTokenValid(reqRes.getToken(), user)).thenReturn(true);
        when(jwtUtils.generateToken(user)).thenReturn("newToken");

        // Act
        ReqRes response = authService.refreshToken(reqRes);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("Successfully Refreshed Token", response.getMessage());
        assertEquals("newToken", response.getToken());
    }

    @Test
    void testRefreshToken_InvalidToken() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setToken("invalidToken");

        OurUsers user = new OurUsers();

        when(jwtUtils.extractUsername(reqRes.getToken())).thenReturn("diaeino@gmail.com");
        when(ourUserRepo.findByEmail("diaeino@gmail.com")).thenReturn(Optional.of(user));
        when(jwtUtils.isTokenValid(reqRes.getToken(), user)).thenReturn(false);

        // Act
        ReqRes response = authService.refreshToken(reqRes);

        // Assert
        assertEquals(500, response.getStatusCode());
    }

    @Test
    void testRefreshToken_UserNotFound() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setToken("validToken");

        when(jwtUtils.extractUsername(reqRes.getToken())).thenReturn("diaeino@gmail.com");
        when(ourUserRepo.findByEmail("diaeino@gmail.com")).thenReturn(Optional.empty());

        // Act
        ReqRes response = authService.refreshToken(reqRes);

        // Assert
        assertEquals(500, response.getStatusCode());
    }
}