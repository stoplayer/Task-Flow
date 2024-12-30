package com.twd.SpringSecurityJWT.dto;

import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReqResTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        ReqRes reqRes = new ReqRes();

        // Act
        reqRes.setStatusCode(200);
        reqRes.setError("Error");
        reqRes.setMessage("Success");
        reqRes.setToken("token123");
        reqRes.setRefreshToken("refreshToken123");
        reqRes.setExpirationTime("2024-12-31T23:59:59");
        reqRes.setName("John Doe");
        reqRes.setEmail("john.doe@example.com");
        reqRes.setRole("USER");
        reqRes.setPassword("password123");
        reqRes.setResetToken("resetToken123");
        reqRes.setCurrentPassword("currentPassword123");
        reqRes.setNewPassword("newPassword123");
        reqRes.setConfirmNewPassword("newPassword123");
        reqRes.setTokenExpirationTime(LocalDateTime.now());

        OurUsers ourUsers = new OurUsers();
        reqRes.setOurUsers(ourUsers);

        Product product = new Product();
        reqRes.setProducts(List.of(product));

        // Assert
        assertThat(reqRes.getStatusCode()).isEqualTo(200);
        assertThat(reqRes.getError()).isEqualTo("Error");
        assertThat(reqRes.getMessage()).isEqualTo("Success");
        assertThat(reqRes.getToken()).isEqualTo("token123");
        assertThat(reqRes.getRefreshToken()).isEqualTo("refreshToken123");
        assertThat(reqRes.getExpirationTime()).isEqualTo("2024-12-31T23:59:59");
        assertThat(reqRes.getName()).isEqualTo("John Doe");
        assertThat(reqRes.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(reqRes.getRole()).isEqualTo("USER");
        assertThat(reqRes.getPassword()).isEqualTo("password123");
        assertThat(reqRes.getResetToken()).isEqualTo("resetToken123");
        assertThat(reqRes.getCurrentPassword()).isEqualTo("currentPassword123");
        assertThat(reqRes.getNewPassword()).isEqualTo("newPassword123");
        assertThat(reqRes.getConfirmNewPassword()).isEqualTo("newPassword123");
        assertThat(reqRes.getTokenExpirationTime()).isNotNull();
        assertThat(reqRes.getOurUsers()).isEqualTo(ourUsers);
        assertThat(reqRes.getProducts()).containsExactly(product);
    }

    @Test
    public void testIsPasswordResetRequestValid() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setEmail("john.doe@example.com");
        reqRes.setNewPassword("newPassword123");

        // Act & Assert
        assertThat(reqRes.isPasswordResetRequestValid()).isTrue();

        // Test invalid cases
        reqRes.setEmail(null);
        assertThat(reqRes.isPasswordResetRequestValid()).isFalse();

        reqRes.setEmail("john.doe@example.com");
        reqRes.setNewPassword("short");
        assertThat(reqRes.isPasswordResetRequestValid()).isFalse();
    }

    @Test
    public void testPasswordsMatch() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setNewPassword("newPassword123");
        reqRes.setConfirmNewPassword("newPassword123");

        // Act & Assert
        assertThat(reqRes.passwordsMatch()).isTrue();

        // Test mismatch
        reqRes.setConfirmNewPassword("differentPassword");
        assertThat(reqRes.passwordsMatch()).isFalse();
    }

    @Test
    public void testResponseStatusEnum() {
        // Arrange & Act
        ReqRes.ResponseStatus success = ReqRes.ResponseStatus.SUCCESS;
        ReqRes.ResponseStatus badRequest = ReqRes.ResponseStatus.BAD_REQUEST;
        ReqRes.ResponseStatus tokenExpired = ReqRes.ResponseStatus.TOKEN_EXPIRED;

        // Assert
        assertThat(success.getCode()).isEqualTo(200);
        assertThat(badRequest.getCode()).isEqualTo(400);
        assertThat(tokenExpired.getCode()).isEqualTo(4002);
    }

    @Test
    public void testToString() {
        // Arrange
        ReqRes reqRes = new ReqRes();
        reqRes.setStatusCode(200);
        reqRes.setMessage("Success");

        // Act
        String toStringResult = reqRes.toString();

        // Assert
        assertThat(toStringResult).contains("statusCode=200");
        assertThat(toStringResult).contains("message=Success");
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        ReqRes reqRes1 = new ReqRes();
        reqRes1.setStatusCode(200);
        reqRes1.setMessage("Success");

        ReqRes reqRes2 = new ReqRes();
        reqRes2.setStatusCode(200);
        reqRes2.setMessage("Success");

        ReqRes reqRes3 = new ReqRes();
        reqRes3.setStatusCode(400);
        reqRes3.setMessage("Bad Request");

        // Act & Assert
        assertThat(reqRes1).isEqualTo(reqRes2);
        assertThat(reqRes1.hashCode()).isEqualTo(reqRes2.hashCode());

        assertThat(reqRes1).isNotEqualTo(reqRes3);
        assertThat(reqRes1.hashCode()).isNotEqualTo(reqRes3.hashCode());
    }
}