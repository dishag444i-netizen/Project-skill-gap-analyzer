
package com.skillpath.backend.repository;

import com.skillpath.backend.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearningProgressRepository
        extends JpaRepository<LearningProgress, Integer> {

    Optional<LearningProgress> findByUserIdAndCareerId(
            Integer userId, Integer careerId);
}
