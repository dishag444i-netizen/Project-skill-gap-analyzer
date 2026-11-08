
package com.skillpath.backend.repository;

import com.skillpath.backend.entity.UserCareer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCareerRepository
        extends JpaRepository<UserCareer, Integer> {

    List<UserCareer> findByUserId(Integer userId);

    Optional<UserCareer> findByUserIdAndCareerId(
            Integer userId, Integer careerId);

    boolean existsByUserIdAndCareerId(
            Integer userId, Integer careerId);

    void deleteByUserIdAndCareerId(
            Integer userId, Integer careerId);
}
