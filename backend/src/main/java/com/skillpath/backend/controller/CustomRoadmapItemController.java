
package com.skillpath.backend.controller;

import com.skillpath.backend.entity.CustomRoadmapItem;
import com.skillpath.backend.entity.User;
import com.skillpath.backend.repository.UserRepository;
import com.skillpath.backend.service.CustomRoadmapItemService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/custom-roadmap")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomRoadmapItemController {

    private final CustomRoadmapItemService service;
    private final UserRepository userRepository;

    public CustomRoadmapItemController(
            CustomRoadmapItemService service,
            UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    // GET CUSTOM TOPICS FOR A CAREER
    @GetMapping("/{careerId}")
    public ResponseEntity<List<CustomRoadmapItem>> getItems(
            Authentication authentication,
            @PathVariable Integer careerId) {

        Integer userId = getUserId(authentication);

        return ResponseEntity.ok(
                service.getItems(userId, careerId));
    }

    // ADD CUSTOM TOPIC
    @PostMapping("/{careerId}")
    public ResponseEntity<?> addItem(
            Authentication authentication,
            @PathVariable Integer careerId,
            @RequestBody Map<String, String> request) {

        Integer userId = getUserId(authentication);

        CustomRoadmapItem item = service.addItem(
                userId,
                careerId,
                request.get("topicName"));

        return ResponseEntity.ok(item);
    }

    // UPDATE COMPLETION STATUS
    @PutMapping("/{id}/completion")
    public ResponseEntity<?> updateCompletion(
            Authentication authentication,
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> request) {

        Integer userId = getUserId(authentication);

        CustomRoadmapItem item = service.updateCompletion(
                id,
                userId,
                request.get("completed"));

        return ResponseEntity.ok(item);
    }

    // DELETE CUSTOM TOPIC
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(
            Authentication authentication,
            @PathVariable Integer id) {

        Integer userId = getUserId(authentication);

        service.deleteItem(id, userId);

        return ResponseEntity.ok(
                Map.of("message", "Custom topic deleted successfully"));
    }

    // GET LOGGED-IN USER ID
    private Integer getUserId(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return Math.toIntExact(user.getId());
    }
}
