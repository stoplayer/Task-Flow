package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.service.PasswordRecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "*")
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ReqRes> forgotPassword(@RequestBody ReqRes request) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                ReqRes response = new ReqRes();
                response.setStatusCode(400);
                response.setMessage("Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            ReqRes response = passwordRecoveryService.initiatePasswordReset(request.getEmail());

            if (response.getStatusCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            ReqRes response = new ReqRes();
            response.setStatusCode(500);
            response.setMessage("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ReqRes> resetPassword(@RequestBody ReqRes request) {
        try {
            // Validate request
            if (request.getResetToken() == null || request.getResetToken().trim().isEmpty()) {
                ReqRes response = new ReqRes();
                response.setStatusCode(400);
                response.setMessage("Reset token is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty() ||
                    request.getConfirmNewPassword() == null || request.getConfirmNewPassword().trim().isEmpty()) {
                ReqRes response = new ReqRes();
                response.setStatusCode(400);
                response.setMessage("New password and confirmation password are required");
                return ResponseEntity.badRequest().body(response);
            }

            ReqRes response = passwordRecoveryService.resetPassword(request);

            if (response.getStatusCode() == 200) {
                return ResponseEntity.ok(response);
            } else if (response.getStatusCode() == 400) {
                return ResponseEntity.badRequest().body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            ReqRes response = new ReqRes();
            response.setStatusCode(500);
            response.setMessage("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/check-token")
    public ResponseEntity<ReqRes> checkToken(@RequestParam String token) {
        try {
            ReqRes response = passwordRecoveryService.validateToken(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ReqRes response = new ReqRes();
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}