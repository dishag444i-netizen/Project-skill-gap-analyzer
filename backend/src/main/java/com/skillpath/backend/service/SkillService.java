
package com.skillpath.backend.service;

import com.skillpath.backend.entity.Skill;
import com.skillpath.backend.repository.SkillRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // ADD SKILL
    public Skill addSkill(Integer userId, String skillName, String category) {

        if (skillName == null || skillName.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name is required");
        }

        Skill skill = new Skill();
        skill.setUserId(userId);
        skill.setSkillName(skillName.trim());
        skill.setCategory(category);
        skill.setCreatedAt(LocalDateTime.now());

        return skillRepository.save(skill);
    }

    // GET ALL USER SKILLS
    public List<Skill> getUserSkills(Integer userId) {
        return skillRepository.findAllByUserId(userId);
    }

    // DELETE SKILL
    @Transactional
    public void deleteSkill(Integer skillId, Integer userId) {

        Skill skill = skillRepository
                .findByIdAndUserId(skillId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Skill not found"));

        skillRepository.delete(skill);
    }
}
