
package com.skillpath.backend.controller;

import com.skillpath.backend.entity.User;
import com.skillpath.backend.entity.UserCareer;
import com.skillpath.backend.repository.UserCareerRepository;
import com.skillpath.backend.repository.UserRepository;
import com.skillpath.backend.repository.CareerPathRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-careers")
@CrossOrigin(origins = "http://localhost:5173")
public class UserCareerController {

    private final UserCareerRepository userCareerRepository;
    private final UserRepository userRepository;
    private final CareerPathRepository careerPathRepository;

    public UserCareerController(
            UserCareerRepository userCareerRepository,
            UserRepository userRepository,
            CareerPathRepository careerPathRepository) {

        this.userCareerRepository = userCareerRepository;
        this.userRepository = userRepository;
        this.careerPathRepository = careerPathRepository;
    }

    // GET SELECTED CAREERS
    @GetMapping
    public ResponseEntity<List<UserCareer>> getUserCareers(
            Authentication authentication) {

        Integer userId = getUserId(authentication);

        return ResponseEntity.ok(
                userCareerRepository.findByUserId(userId));
    }

    // SELECT A CAREER
    @PostMapping("/{careerId}")
    public ResponseEntity<?> selectCareer(
            Authentication authentication,
            @PathVariable Integer careerId) {

        Integer userId = getUserId(authentication);

        if (!careerPathRepository.existsById(careerId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Career not found"));
        }

        if (userCareerRepository.existsByUserIdAndCareerId(
                userId, careerId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Career already selected"));
        }

        UserCareer userCareer = new UserCareer();
        userCareer.setUserId(userId);
        userCareer.setCareerId(careerId);
        userCareer.setSelectedAt(LocalDateTime.now());

        return ResponseEntity.ok(
                userCareerRepository.save(userCareer));
    }

    // REMOVE A SELECTED CAREER
    @DeleteMapping("/{careerId}")
    public ResponseEntity<?> removeCareer(
            Authentication authentication,
            @PathVariable Integer careerId) {

        Integer userId = getUserId(authentication);

        userCareerRepository.deleteByUserIdAndCareerId(
                userId, careerId);

        return ResponseEntity.ok(
                Map.of("message", "Career removed successfully"));
    }

    // GET LOGGED-IN USER ID
    private Integer getUserId(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return Math.toIntExact(user.getId());
    }
}
