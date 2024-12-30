package com.twd.SpringSecurityJWT.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.Product;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqRes {

    // Existing fields
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String email;
    private String role;
    private String password;
    private List<Product> products;
    private OurUsers ourUsers;

    // New password recovery fields
    private String resetToken;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
    private LocalDateTime tokenExpirationTime;

    // Password validation methods
    public boolean isPasswordResetRequestValid() {
        return email != null && !email.isEmpty() &&
                newPassword != null && newPassword.length() >= 8;
    }

    public boolean passwordsMatch() {
        return newPassword != null &&
                confirmNewPassword != null &&
                newPassword.equals(confirmNewPassword);
    }



    // Additional password strength validation (optional)


    // Enum for standardized status codes
    public enum ResponseStatus {
        SUCCESS(200),
        CREATED(201),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        PASSWORD_RESET_REQUIRED(4001),
        TOKEN_EXPIRED(4002),
        INTERNAL_SERVER_ERROR(500);

        private final int code;

        ResponseStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}