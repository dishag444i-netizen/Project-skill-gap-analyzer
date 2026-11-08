
package com.skillpath.backend.service;

import com.skillpath.backend.entity.Roadmap;
import com.skillpath.backend.entity.CareerSkill;
import com.skillpath.backend.repository.RoadmapRepository;
import com.skillpath.backend.repository.CareerSkillRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final CareerSkillRepository careerSkillRepository;

    public RoadmapService(
            RoadmapRepository roadmapRepository,
            CareerSkillRepository careerSkillRepository) {
        this.roadmapRepository = roadmapRepository;
        this.careerSkillRepository = careerSkillRepository;
    }

    @Transactional
    public List<Roadmap> getRoadmap(Integer userId, Integer careerId) {

        List<CareerSkill> careerSkills =
                careerSkillRepository.findByCareerId(careerId);

        for (CareerSkill skill : careerSkills) {

            if (!roadmapRepository.existsByUserIdAndCareerIdAndTopicName(
                    userId, careerId, skill.getSkillName())) {

                Roadmap roadmap = new Roadmap();
                roadmap.setUserId(userId);
                roadmap.setCareerId(careerId);
                roadmap.setTopicName(skill.getSkillName());
                roadmap.setIsCompleted(false);
                roadmap.setCreatedAt(LocalDateTime.now());

                roadmapRepository.save(roadmap);
            }
        }

        return roadmapRepository.findByUserIdAndCareerId(userId, careerId);
    }

    public Roadmap addTopic(Integer userId, Integer careerId, String topicName) {

        if (topicName == null || topicName.trim().isEmpty()) {
            throw new RuntimeException("Topic name cannot be empty");
        }

        String cleanedTopic = topicName.trim();

        if (roadmapRepository.existsByUserIdAndCareerIdAndTopicName(
                userId, careerId, cleanedTopic)) {
            throw new RuntimeException("Topic already exists");
        }

        Roadmap roadmap = new Roadmap();
        roadmap.setUserId(userId);
        roadmap.setCareerId(careerId);
        roadmap.setTopicName(cleanedTopic);
        roadmap.setIsCompleted(false);
        roadmap.setCreatedAt(LocalDateTime.now());

        return roadmapRepository.save(roadmap);
    }

    @Transactional
    public Roadmap updateCompletion(
            Integer roadmapId, Integer userId, Boolean completed) {

        Roadmap roadmap = roadmapRepository
                .findByIdAndUserId(roadmapId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Roadmap topic not found"));

        roadmap.setIsCompleted(completed);
        return roadmapRepository.save(roadmap);
    }

    public void deleteTopic(Integer roadmapId, Integer userId) {

        Roadmap roadmap = roadmapRepository
                .findByIdAndUserId(roadmapId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Roadmap topic not found"));

        roadmapRepository.delete(roadmap);
    }
}
