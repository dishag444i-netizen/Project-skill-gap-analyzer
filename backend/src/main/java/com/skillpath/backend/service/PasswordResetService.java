
package com.skillpath.backend.service;

import com.skillpath.backend.entity.PasswordResetToken;
import com.skillpath.backend.entity.User;
import com.skillpath.backend.repository.PasswordResetTokenRepository;
import com.skillpath.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            UserService userService,
            EmailService emailService) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    // GENERATE PASSWORD RESET TOKEN
    @Transactional
    public String generateResetToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Integer userId = Math.toIntExact(user.getId());

        tokenRepository.deleteByUserId(userId);

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUserId(userId);
        resetToken.setToken(token);
        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(resetToken);

        // SEND PASSWORD RESET EMAIL
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return token;
    }

    // VERIFY TOKEN AND RESET PASSWORD
    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid reset token"));

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            tokenRepository.delete(resetToken);
            throw new RuntimeException("Reset token has expired");
        }

        Long userId = Long.valueOf(resetToken.getUserId());

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        userService.updatePassword(user.getEmail(), newPassword);

        tokenRepository.delete(resetToken);
    }
}
