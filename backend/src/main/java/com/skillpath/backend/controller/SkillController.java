
package com.skillpath.backend.controller;

import com.skillpath.backend.entity.Skill;
import com.skillpath.backend.entity.User;
import com.skillpath.backend.repository.UserRepository;
import com.skillpath.backend.service.SkillService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class SkillController {

    private final SkillService skillService;
    private final UserRepository userRepository;

    public SkillController(
            SkillService skillService,
            UserRepository userRepository) {
        this.skillService = skillService;
        this.userRepository = userRepository;
    }

    // GET USER SKILLS
    @GetMapping
    public ResponseEntity<List<Skill>> getSkills(Authentication authentication) {

        Integer userId = getUserId(authentication);

        return ResponseEntity.ok(
                skillService.getUserSkills(userId));
    }

    // ADD SKILL
    @PostMapping
    public ResponseEntity<?> addSkill(
            Authentication authentication,
            @RequestBody Map<String, String> request) {

        Integer userId = getUserId(authentication);

        Skill skill = skillService.addSkill(
                userId,
                request.get("skillName"),
                request.get("category")
        );

        return ResponseEntity.ok(skill);
    }

    // DELETE SKILL
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSkill(
            Authentication authentication,
            @PathVariable Integer id) {

        Integer userId = getUserId(authentication);

        skillService.deleteSkill(id, userId);

        return ResponseEntity.ok(
                Map.of("message", "Skill deleted successfully"));
    }

    // GET LOGGED-IN USER ID
    private Integer getUserId(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return Math.toIntExact(user.getId());
    }
}
