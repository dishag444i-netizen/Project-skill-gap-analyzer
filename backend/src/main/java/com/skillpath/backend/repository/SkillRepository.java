
package com.skillpath.backend.repository;

import com.skillpath.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Integer> {

    List<Skill> findAllByUserId(Integer userId);

    Optional<Skill> findByIdAndUserId(Integer id, Integer userId);
}
