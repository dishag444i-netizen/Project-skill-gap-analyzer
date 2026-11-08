
package com.skillpath.backend.controller;

import com.skillpath.backend.dto.ForgotPasswordRequest;
import com.skillpath.backend.service.PasswordResetService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(
            PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // FORGOT PASSWORD API
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        try {
            passwordResetService.generateResetToken(request.getEmail());

            return ResponseEntity.ok(
                    Map.of("message",
                    "If the account exists, password reset instructions will be sent.")
            );

        } catch (RuntimeException e) {
            // Do not reveal whether an email is registered.
            return ResponseEntity.ok(
                    Map.of("message",
                    "If the account exists, password reset instructions will be sent.")
            );
        }
    }

    // RESET PASSWORD API
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        try {
            if (newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message",
                                "Password must contain at least 6 characters."));
            }

            passwordResetService.resetPassword(token, newPassword);

            return ResponseEntity.ok(
                    Map.of("message", "Password reset successful!")
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
