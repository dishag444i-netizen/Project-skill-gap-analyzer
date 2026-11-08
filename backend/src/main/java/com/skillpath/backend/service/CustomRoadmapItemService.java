
package com.skillpath.backend.service;

import com.skillpath.backend.entity.CustomRoadmapItem;
import com.skillpath.backend.repository.CustomRoadmapItemRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomRoadmapItemService {

    private final CustomRoadmapItemRepository repository;

    public CustomRoadmapItemService(
            CustomRoadmapItemRepository repository) {
        this.repository = repository;
    }

    // GET CUSTOM TOPICS FOR A CAREER
    public List<CustomRoadmapItem> getItems(
            Integer userId, Integer careerId) {
        return repository.findByUserIdAndCareerId(userId, careerId);
    }

    // ADD CUSTOM TOPIC
    public CustomRoadmapItem addItem(
            Integer userId, Integer careerId, String topicName) {

        if (topicName == null || topicName.trim().isEmpty()) {
            throw new RuntimeException("Topic name cannot be empty");
        }

        String cleanedTopic = topicName.trim();

        if (repository.existsByUserIdAndCareerIdAndTopicName(
                userId, careerId, cleanedTopic)) {
            throw new RuntimeException("Topic already exists");
        }

        CustomRoadmapItem item = new CustomRoadmapItem();
        item.setUserId(userId);
        item.setCareerId(careerId);
        item.setTopicName(cleanedTopic);
        item.setIsCompleted(false);
        item.setCreatedAt(LocalDateTime.now());

        return repository.save(item);
    }

    // UPDATE COMPLETION STATUS
    @Transactional
    public CustomRoadmapItem updateCompletion(
            Integer itemId, Integer userId, Boolean completed) {

        CustomRoadmapItem item = repository
                .findByIdAndUserId(itemId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Custom topic not found"));

        item.setIsCompleted(completed);
        return repository.save(item);
    }

    // DELETE CUSTOM TOPIC
    public void deleteItem(Integer itemId, Integer userId) {

        CustomRoadmapItem item = repository
                .findByIdAndUserId(itemId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Custom topic not found"));

        repository.delete(item);
    }
}
