
package com.skillpath.backend.repository;

import com.skillpath.backend.entity.CustomRoadmapItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomRoadmapItemRepository
        extends JpaRepository<CustomRoadmapItem, Integer> {

    List<CustomRoadmapItem> findByUserIdAndCareerId(
            Integer userId, Integer careerId);

    Optional<CustomRoadmapItem> findByIdAndUserId(
            Integer id, Integer userId);

    boolean existsByUserIdAndCareerIdAndTopicName(
            Integer userId, Integer careerId, String topicName);
}
