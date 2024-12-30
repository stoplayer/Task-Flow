package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private OurUserRepo ourUserRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public ReqRes signUp(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();
        try {
            // Validate email format using regex
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!registrationRequest.getEmail().matches(emailRegex)) {
                throw new IllegalArgumentException("Invalid email format");
            }

            // Check if email is unique
            Optional<OurUsers> existingUser = ourUserRepo.findByEmail(registrationRequest.getEmail());
            if (existingUser.isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }

            // Validate password format using regex
            String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
            if (!registrationRequest.getPassword().matches(passwordRegex)) {
                throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace");
            }

            OurUsers ourUsers = new OurUsers();
            ourUsers.setEmail(registrationRequest.getEmail());

            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUsers.setRole(registrationRequest.getRole());
            ourUsers.setName(registrationRequest.getName());

            // Check if the role is null or empty, and default to "USER"
            String role = registrationRequest.getRole();
            if (role == null || role.trim().isEmpty()) {
                role = "USER";  // Default role
            }
            ourUsers.setRole(role);

            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
            if (ourUserResult != null && ourUserResult.getId()>0) {
                resp.setOurUsers(ourUserResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes signIn(ReqRes signinRequest) {
        ReqRes response = new ReqRes();

        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword())
            );

            // Retrieve the user from the repository
            OurUsers user = ourUserRepo.findByEmail(signinRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get the user ID from the repository
            Integer userId = user.getId(); // Ensure `OurUsers` has the `getId()` method implemented

            // Generate tokens
            String jwt = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            // Set response fields
            response.setStatusCode(200);
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setMessage("Successfully Signed In");
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");

            // Use the user ID
            response.setOurUsers(user); // Assign the entire user object if necessary
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }

        return response;
    }


    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
        OurUsers users = ourUserRepo.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenReqiest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }
}
