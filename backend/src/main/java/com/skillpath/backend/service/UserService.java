package com.skillpath.backend.service;

import com.skillpath.backend.entity.User;
import com.skillpath.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // REGISTER USER
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already registered!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // FIND USER BY EMAIL
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // CHECK PASSWORD
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // UPDATE USER PROFILE
    public User updateProfile(String email, String fullName) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new RuntimeException("Name cannot be empty!");
        }

        user.setFullName(fullName.trim());

        return userRepository.save(user);
    }

    // UPDATE PASSWORD
    public void updatePassword(String email, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}