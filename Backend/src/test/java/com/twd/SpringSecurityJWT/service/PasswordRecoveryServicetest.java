package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.PasswordResetToken;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordRecoveryServicetest {

    @Mock
    private OurUserRepo userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private PasswordRecoveryService passwordRecoveryService;

    @Test
    void testInitiatePasswordReset_validEmail() {
        String email = "test@example.com";
        OurUsers user = new OurUsers();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenRepository.hasValidToken(any(), any())).thenReturn(false);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(new PasswordResetToken());
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        ReqRes response = passwordRecoveryService.initiatePasswordReset(email);

        assertEquals(200, response.getStatusCode());
        assertEquals("Password reset link sent successfully to your email", response.getMessage());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testInitiatePasswordReset_invalidEmail() {
        String email = "invalid@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ReqRes response = passwordRecoveryService.initiatePasswordReset(email);

        assertEquals(404, response.getStatusCode());
        assertEquals("No user found with the provided email address", response.getMessage());
    }

    @Test
    void testInitiatePasswordReset_emptyEmail() {
        String email = "";

        ReqRes response = passwordRecoveryService.initiatePasswordReset(email);

        assertEquals(400, response.getStatusCode());
        assertEquals("Email is required", response.getMessage());
    }

    @Test
    void testInitiatePasswordReset_existingValidToken() {
        String email = "test@example.com";
        OurUsers user = new OurUsers();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenRepository.hasValidToken(any(), any())).thenReturn(true);

        ReqRes response = passwordRecoveryService.initiatePasswordReset(email);

        assertEquals(400, response.getStatusCode());
        assertEquals("A valid reset token already exists. Please check your email or wait for the current token to expire.", response.getMessage());
    }

    @Test
    void testResetPassword_validToken() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";
        ReqRes request = new ReqRes();
        request.setResetToken(token);
        request.setNewPassword(newPassword);
        request.setConfirmNewPassword(confirmPassword);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        OurUsers user = new OurUsers();
        user.setPassword("oldPassword123");

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        ReqRes response = passwordRecoveryService.resetPassword(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("Password has been reset successfully", response.getMessage());
        verify(tokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void testResetPassword_invalidToken() {
        String invalidToken = UUID.randomUUID().toString();
        ReqRes request = new ReqRes();
        request.setResetToken(invalidToken);

        when(tokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        ReqRes response = passwordRecoveryService.resetPassword(request);

        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid or expired token", response.getMessage());
    }

    @Test
    void testResetPassword_expiredToken() {
        String token = UUID.randomUUID().toString();
        ReqRes request = new ReqRes();
        request.setResetToken(token);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().minusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        ReqRes response = passwordRecoveryService.resetPassword(request);

        assertEquals(400, response.getStatusCode());
        assertEquals("Token has expired", response.getMessage());
        verify(tokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void testResetPassword_shortPassword() {
        String token = UUID.randomUUID().toString();
        ReqRes request = new ReqRes();
        request.setResetToken(token);
        request.setNewPassword("short");
        request.setConfirmNewPassword("short");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        ReqRes response = passwordRecoveryService.resetPassword(request);

        assertEquals(400, response.getStatusCode());
        assertEquals("Password must be at least 8 characters long", response.getMessage());
    }

    @Test
    void testResetPassword_passwordsDoNotMatch() {
        String token = UUID.randomUUID().toString();
        ReqRes request = new ReqRes();
        request.setResetToken(token);
        request.setNewPassword("newPassword123");
        request.setConfirmNewPassword("differentPassword");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        ReqRes response = passwordRecoveryService.resetPassword(request);

        assertEquals(400, response.getStatusCode());
        assertEquals("Passwords do not match", response.getMessage());
    }

    @Test
    void testResetPassword_newPasswordSameAsCurrent() {
        String token = UUID.randomUUID().toString();
        ReqRes request = new ReqRes();
        request.setResetToken(token);
        request.setNewPassword("currentPassword");
        request.setConfirmNewPassword("currentPassword");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        OurUsers user = new OurUsers();
        user.setPassword("currentPassword");

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        ReqRes response = passwordRecoveryService.resetPassword(request);

        assertEquals(400, response.getStatusCode());
        assertEquals("New password must be different from the current password", response.getMessage());
    }

    @Test
    void testValidateToken_validToken() {
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        ReqRes response = passwordRecoveryService.validateToken(token);

        assertEquals(200, response.getStatusCode());
        assertEquals("Token is valid", response.getMessage());
    }

    @Test
    void testValidateToken_invalidToken() {
        String token = UUID.randomUUID().toString();

        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        ReqRes response = passwordRecoveryService.validateToken(token);

        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid token", response.getMessage());
    }

    @Test
    void testValidateToken_expiredToken() {
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().minusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        ReqRes response = passwordRecoveryService.validateToken(token);

        assertEquals(400, response.getStatusCode());
        assertEquals("Token has expired", response.getMessage());
        verify(tokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void testCleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken expiredToken = new PasswordResetToken();
        expiredToken.setExpiryDate(now.minusHours(1));

        PasswordResetToken validToken = new PasswordResetToken();
        validToken.setExpiryDate(now.plusHours(1));

        when(tokenRepository.findAll()).thenReturn(List.of(expiredToken, validToken));

        passwordRecoveryService.cleanupExpiredTokens();

        verify(tokenRepository, times(1)).delete(expiredToken);
        verify(tokenRepository, never()).delete(validToken);
    }
}