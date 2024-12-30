package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.Product;
import com.twd.SpringSecurityJWT.repository.ProductRepo;
import com.twd.SpringSecurityJWT.service.OurUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminUsers {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OurUserDetailsService ourUserDetailsService;

    @GetMapping("/public/product")
    public ResponseEntity<Object> getAllProducts() {
        return ResponseEntity.ok(productRepo.findAll());
    }

    @PostMapping("/admin/saveproduct")
    public ResponseEntity<Object> signUp(@RequestBody ReqRes productRequest) {
        Product productToSave = new Product();
        productToSave.setName(productRequest.getName());
        return ResponseEntity.ok(productRepo.save(productToSave));
    }

    @GetMapping("/user/alone")
    public ResponseEntity<Object> userAlone() {
        return ResponseEntity.ok("Users alone can access this API only");
    }

    @GetMapping("/adminuser/both")
    public ResponseEntity<Object> bothAdminAndUsersApi() {
        return ResponseEntity.ok("Both Admin and Users Can access the api");
    }

    @GetMapping("/public/email")
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/adminuser/all-users")
    public ResponseEntity<List<OurUsers>> getAllUsers() {
        List<OurUsers> users = ourUserDetailsService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Profile Picture Endpoints

    @PostMapping("/adminuser/profile-picture")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file) {
        try {
            // Check if file is null or empty
            if (file == null || file.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "No file provided. Please upload a valid image file."));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "Only image files (e.g., jpg, png) are allowed."));
            }

            // Validate file size (e.g., max 15MB)
            if (file.getSize() > 15 * 1024 * 1024) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "File size should not exceed 5MB."));
            }

            // Get the authenticated user's email
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // Update the profile picture
            ourUserDetailsService.updateProfilePicture(email, file);

            // Success response
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile picture uploaded successfully.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            // Handle exceptions
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload profile picture: " + e.getMessage()));
        }
    }

    @GetMapping("/user/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture() {
        // Get the authenticated user's email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Check if the user has a profile picture
        if (!ourUserDetailsService.hasProfilePicture(email)) {
            return ResponseEntity.notFound().build();
        }

        // Retrieve the profile picture data
        byte[] imageData = ourUserDetailsService.getProfilePicture(email);
        String contentType = ourUserDetailsService.getProfilePictureType(email);

        // Build response with headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));

        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

    @DeleteMapping("/user/profile-picture")
    public ResponseEntity<Map<String, String>> deleteProfilePicture() {
        // Get the authenticated user's email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Check if the user has a profile picture
        if (!ourUserDetailsService.hasProfilePicture(email)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No profile picture found for the user."));
        }

        // Delete the profile picture
        ourUserDetailsService.deleteProfilePicture(email);

        // Success response
        return ResponseEntity.ok(Map.of("message", "Profile picture deleted successfully."));
    }

    @GetMapping("/adminuser/user/{email}/profile-picture")
    public ResponseEntity<byte[]> getUserProfilePicture(@PathVariable String email) {
        // Check if the specified user has a profile picture
        if (!ourUserDetailsService.hasProfilePicture(email)) {
            return ResponseEntity.notFound().build();
        }

        // Retrieve the profile picture data
        byte[] imageData = ourUserDetailsService.getProfilePicture(email);
        String contentType = ourUserDetailsService.getProfilePictureType(email);

        // Build response with headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));

        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

}