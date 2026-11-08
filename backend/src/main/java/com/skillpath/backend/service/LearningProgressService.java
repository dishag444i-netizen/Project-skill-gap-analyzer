
package com.skillpath.backend.service;

import com.skillpath.backend.entity.LearningProgress;
import com.skillpath.backend.entity.Roadmap;
import com.skillpath.backend.entity.CustomRoadmapItem;

import com.skillpath.backend.repository.LearningProgressRepository;
import com.skillpath.backend.repository.RoadmapRepository;
import com.skillpath.backend.repository.CustomRoadmapItemRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LearningProgressService {

    private final LearningProgressRepository progressRepository;
    private final RoadmapRepository roadmapRepository;
    private final CustomRoadmapItemRepository customRepository;

    public LearningProgressService(
            LearningProgressRepository progressRepository,
            RoadmapRepository roadmapRepository,
            CustomRoadmapItemRepository customRepository) {

        this.progressRepository = progressRepository;
        this.roadmapRepository = roadmapRepository;
        this.customRepository = customRepository;
    }

    // CALCULATE AND SAVE CAREER PROGRESS
    @Transactional
    public LearningProgress calculateProgress(
            Integer userId, Integer careerId) {

        List<Roadmap> roadmapItems =
                roadmapRepository.findByUserIdAndCareerId(
                        userId, careerId);

        List<CustomRoadmapItem> customItems =
                customRepository.findByUserIdAndCareerId(
                        userId, careerId);

        int totalTopics = roadmapItems.size() + customItems.size();

        int completedTopics = (int) roadmapItems.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsCompleted()))
                .count();

        completedTopics += (int) customItems.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsCompleted()))
                .count();

        double percentage = totalTopics == 0
                ? 0.0
                : Math.round(completedTopics * 10000.0 / totalTopics) / 100.0;

        LearningProgress progress = progressRepository
                .findByUserIdAndCareerId(userId, careerId)
                .orElseGet(LearningProgress::new);

        progress.setUserId(userId);
        progress.setCareerId(careerId);
        progress.setCompletedTopics(completedTopics);
        progress.setTotalTopics(totalTopics);
        progress.setProgressPercentage(percentage);
        progress.setUpdatedAt(LocalDateTime.now());

        return progressRepository.save(progress);
    }

    // GET PROGRESS FOR A CAREER
    public LearningProgress getProgress(
            Integer userId, Integer careerId) {

        return calculateProgress(userId, careerId);
    }
}
