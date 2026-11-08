
package com.skillpath.backend.controller;

import com.skillpath.backend.entity.Roadmap;
import com.skillpath.backend.entity.User;
import com.skillpath.backend.repository.UserRepository;
import com.skillpath.backend.service.RoadmapService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roadmap")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final UserRepository userRepository;

    public RoadmapController(
            RoadmapService roadmapService,
            UserRepository userRepository) {
        this.roadmapService = roadmapService;
        this.userRepository = userRepository;
    }

    // GET ROADMAP FOR A CAREER
    @GetMapping("/{careerId}")
    public ResponseEntity<List<Roadmap>> getRoadmap(
            Authentication authentication,
            @PathVariable Integer careerId) {

        Integer userId = getUserId(authentication);

        return ResponseEntity.ok(
                roadmapService.getRoadmap(userId, careerId));
    }

    // ADD A ROADMAP TOPIC
    @PostMapping("/{careerId}")
    public ResponseEntity<?> addTopic(
            Authentication authentication,
            @PathVariable Integer careerId,
            @RequestBody Map<String, String> request) {

        Integer userId = getUserId(authentication);

        Roadmap roadmap = roadmapService.addTopic(
                userId,
                careerId,
                request.get("topicName"));

        return ResponseEntity.ok(roadmap);
    }

    // UPDATE TOPIC COMPLETION
    @PutMapping("/{id}/completion")
    public ResponseEntity<?> updateCompletion(
            Authentication authentication,
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> request) {

        Integer userId = getUserId(authentication);

        Roadmap roadmap = roadmapService.updateCompletion(
                id,
                userId,
                request.get("completed"));

        return ResponseEntity.ok(roadmap);
    }

    // DELETE A ROADMAP TOPIC
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(
            Authentication authentication,
            @PathVariable Integer id) {

        Integer userId = getUserId(authentication);

        roadmapService.deleteTopic(id, userId);

        return ResponseEntity.ok(
                Map.of("message", "Roadmap topic deleted successfully"));
    }

    // GET LOGGED-IN USER ID
    private Integer getUserId(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return Math.toIntExact(user.getId());
    }
}
