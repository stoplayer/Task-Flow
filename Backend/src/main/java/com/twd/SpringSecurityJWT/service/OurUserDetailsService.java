package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class OurUserDetailsService implements UserDetailsService {

    @Autowired
    private OurUserRepo ourUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return ourUserRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    // Get all users
    public List<OurUsers> getAllUsers() {
        return ourUserRepo.findAll();
    }

    // Upload profile picture
    public void updateProfilePicture(String email, MultipartFile file) throws IOException {
        OurUsers user = ourUserRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        user.setProfilePicture(
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename()
        );

        ourUserRepo.save(user);
    }

    // Get user's profile picture
    public byte[] getProfilePicture(String email) {
        OurUsers user = ourUserRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return user.getProfilePicture();
    }

    // Get profile picture content type
    public String getProfilePictureType(String email) {
        OurUsers user = ourUserRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return user.getProfilePictureType();
    }

    // Delete profile picture
    public void deleteProfilePicture(String email) {
        OurUsers user = ourUserRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        user.setProfilePicture(null, null, null);
        ourUserRepo.save(user);
    }

    // Check if user has profile picture
    public boolean hasProfilePicture(String email) {
        Optional<OurUsers> userOptional = ourUserRepo.findByEmail(email);
        return userOptional.map(user -> user.getProfilePicture() != null).orElse(false);
    }

    // Get user by email
    public OurUsers getUserByEmail(String email) {
        return ourUserRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}