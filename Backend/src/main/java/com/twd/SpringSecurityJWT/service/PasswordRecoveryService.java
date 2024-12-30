package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.PasswordResetToken;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    @Autowired
    private OurUserRepo userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public ReqRes initiatePasswordReset(String email) {
        ReqRes response = new ReqRes();

        try {
            if (email == null || email.trim().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("Email is required");
                return response;
            }

            OurUsers user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Clean up expired tokens for all users before creating new one
            cleanupExpiredTokens();

            // Check if user already has a valid token
            if (tokenRepository.hasValidToken(user, LocalDateTime.now())) {
                response.setStatusCode(400);
                response.setMessage("A valid reset token already exists. Please check your email or wait for the current token to expire.");
                return response;
            }

            // Delete any existing reset tokens for this user
            tokenRepository.deleteByUser(user);

            // Generate new reset token
            String token = generateUniqueToken();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

            tokenRepository.save(resetToken);

            // Send reset email
            sendPasswordResetEmail(user, token);

            response.setStatusCode(200);
            response.setMessage("Password reset link sent successfully to your email");
            response.setResetToken(token);
        } catch (RuntimeException ex) {
            if (ex.getMessage().equals("User not found")) {
                response.setStatusCode(404);
                response.setMessage("No user found with the provided email address");
            } else {
                response.setStatusCode(500);
                response.setMessage("An error occurred while processing your request: " + ex.getMessage());
            }
        }

        return response;
    }

    @Transactional
    public ReqRes resetPassword(ReqRes request) {
        ReqRes response = new ReqRes();

        try {
            // Validate input
            if (request.getResetToken() == null || request.getResetToken().trim().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("Reset token is required");
                return response;
            }

            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("New password is required");
                return response;
            }

            // Clean up expired tokens before processing
            cleanupExpiredTokens();

            // Find and validate token
            PasswordResetToken resetToken = tokenRepository.findByToken(request.getResetToken())
                    .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

            // Check token expiry
            if (resetToken.isExpired()) {
                tokenRepository.delete(resetToken);
                throw new RuntimeException("Token has expired");
            }

            // Validate password requirements
            if (request.getNewPassword().length() < 8) {
                throw new RuntimeException("Password must be at least 8 characters long");
            }

            // Validate passwords match
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                throw new RuntimeException("Passwords do not match");
            }

            // Update user password
            OurUsers user = resetToken.getUser();
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            // Check if new password is different from the current one
            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                throw new RuntimeException("New password must be different from the current password");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // Delete used token
            tokenRepository.delete(resetToken);

            response.setStatusCode(200);
            response.setMessage("Password has been reset successfully");
        } catch (RuntimeException ex) {
            response.setStatusCode(400);
            response.setMessage(ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("An unexpected error occurred while resetting your password");
        }

        return response;
    }

    @Transactional
    public ReqRes validateToken(String token) {
        ReqRes response = new ReqRes();

        try {
            if (token == null || token.trim().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("Token is required");
                return response;
            }

            // Clean up expired tokens first
            cleanupExpiredTokens();

            // Find and validate token
            PasswordResetToken resetToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Invalid token"));

            if (resetToken.isExpired()) {
                tokenRepository.delete(resetToken);
                throw new RuntimeException("Token has expired");
            }

            response.setStatusCode(200);
            response.setMessage("Token is valid");
        } catch (RuntimeException ex) {
            response.setStatusCode(400);
            response.setMessage(ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("An error occurred while validating the token");
        }

        return response;
    }

    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteAllExpiredTokens(now);
    }

    private String generateUniqueToken() {
        return UUID.randomUUID().toString();
    }

    private void sendPasswordResetEmail(OurUsers user, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("To reset your password, please use the following token: " + token +
                "\n\nThis token will expire in 1 hour." +
                "\n\nIf you didn't request this password reset, please ignore this email.");

        mailSender.send(mailMessage);
    }
}