
package com.skillpath.backend.repository;

import com.skillpath.backend.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Integer> {

    List<Roadmap> findByUserIdAndCareerId(Integer userId, Integer careerId);

    Optional<Roadmap> findByIdAndUserId(Integer id, Integer userId);

    boolean existsByUserIdAndCareerIdAndTopicName(
            Integer userId, Integer careerId, String topicName);
}
