
package com.skillpath.backend.controller;

import com.skillpath.backend.config.JwtUtil;
import com.skillpath.backend.dto.LoginRequest;
import com.skillpath.backend.dto.RegisterRequest;
import com.skillpath.backend.dto.UserResponse;
import com.skillpath.backend.entity.User;
import com.skillpath.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // REGISTER API
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        try {
            User user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());

            User savedUser = userService.registerUser(user);

            return ResponseEntity.ok(new UserResponse(savedUser));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // LOGIN API
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginRequest request) {

        try {
            User existingUser = userService
                    .getUserByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            boolean passwordMatches = userService.checkPassword(
                    request.getPassword(),
                    existingUser.getPassword()
            );

            if (!passwordMatches) {
                return ResponseEntity.badRequest()
                        .body("Invalid email or password!");
            }

            String token = jwtUtil.generateToken(existingUser.getEmail());

            UserResponse userResponse = new UserResponse(existingUser);

            return ResponseEntity.ok(
                    Map.of(
                            "token", token,
                            "user", userResponse
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid email or password!");
        }
    }

    // GET PROFILE API
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return ResponseEntity.ok(new UserResponse(user));
    }

    // UPDATE PROFILE API
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody Map<String, String> request) {

        try {
            User updatedUser = userService.updateProfile(
                    authentication.getName(),
                    request.get("fullName")
            );

            return ResponseEntity.ok(new UserResponse(updatedUser));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
