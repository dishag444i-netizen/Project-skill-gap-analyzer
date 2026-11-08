
package com.skillpath.backend.controller;

import com.skillpath.backend.entity.LearningProgress;
import com.skillpath.backend.entity.User;
import com.skillpath.backend.repository.UserRepository;
import com.skillpath.backend.service.LearningProgressService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "http://localhost:5173")
public class LearningProgressController {

    private final LearningProgressService progressService;
    private final UserRepository userRepository;

    public LearningProgressController(
            LearningProgressService progressService,
            UserRepository userRepository) {

        this.progressService = progressService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{careerId}")
    public ResponseEntity<LearningProgress> getProgress(
            Authentication authentication,
            @PathVariable Integer careerId) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Integer userId = Math.toIntExact(user.getId());

        return ResponseEntity.ok(
                progressService.calculateProgress(userId, careerId));
    }

    @PostMapping("/{careerId}/refresh")
    public ResponseEntity<LearningProgress> refreshProgress(
            Authentication authentication,
            @PathVariable Integer careerId) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Integer userId = Math.toIntExact(user.getId());

        return ResponseEntity.ok(
                progressService.calculateProgress(userId, careerId));
    }
}
