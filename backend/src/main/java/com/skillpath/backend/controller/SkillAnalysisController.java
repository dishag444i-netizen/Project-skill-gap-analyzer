
package com.skillpath.backend.controller;

import com.skillpath.backend.entity.User;
import com.skillpath.backend.repository.UserRepository;
import com.skillpath.backend.service.SkillAnalysisService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/skill-analysis")
@CrossOrigin(origins = "http://localhost:5173")
public class SkillAnalysisController {

    private final SkillAnalysisService skillAnalysisService;
    private final UserRepository userRepository;

    public SkillAnalysisController(
            SkillAnalysisService skillAnalysisService,
            UserRepository userRepository) {

        this.skillAnalysisService = skillAnalysisService;
        this.userRepository = userRepository;
    }

    // ANALYZE SKILLS FOR SELECTED CAREER
    @GetMapping("/{careerId}")
    public ResponseEntity<Map<String, Object>> analyzeSkills(
            Authentication authentication,
            @PathVariable Integer careerId) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Integer userId = Math.toIntExact(user.getId());

        return ResponseEntity.ok(
                skillAnalysisService.analyzeSkills(userId, careerId));
    }
}
