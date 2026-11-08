
package com.skillpath.backend.service;

import com.skillpath.backend.entity.Skill;
import com.skillpath.backend.entity.CareerSkill;
import com.skillpath.backend.repository.SkillRepository;
import com.skillpath.backend.repository.CareerSkillRepository;
import com.skillpath.backend.repository.CareerPathRepository;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillAnalysisService {

    private final SkillRepository skillRepository;
    private final CareerSkillRepository careerSkillRepository;
    private final CareerPathRepository careerPathRepository;

    public SkillAnalysisService(
            SkillRepository skillRepository,
            CareerSkillRepository careerSkillRepository,
            CareerPathRepository careerPathRepository) {

        this.skillRepository = skillRepository;
        this.careerSkillRepository = careerSkillRepository;
        this.careerPathRepository = careerPathRepository;
    }

    // ANALYZE USER SKILLS FOR A CAREER
    public Map<String, Object> analyzeSkills(
            Integer userId, Integer careerId) {

        careerPathRepository.findById(careerId)
                .orElseThrow(() ->
                        new RuntimeException("Career path not found"));

        List<Skill> userSkills =
                skillRepository.findAllByUserId(userId);

        List<CareerSkill> requiredSkills =
                careerSkillRepository.findByCareerId(careerId);

        Set<String> userSkillNames = userSkills.stream()
                .map(skill -> skill.getSkillName().trim().toLowerCase())
                .collect(Collectors.toSet());

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (CareerSkill required : requiredSkills) {

            String skillName = required.getSkillName();

            if (userSkillNames.contains(
                    skillName.trim().toLowerCase())) {

                matchedSkills.add(skillName);

            } else {
                missingSkills.add(skillName);
            }
        }

        int totalSkills = requiredSkills.size();
        int matchedCount = matchedSkills.size();

        double percentage = totalSkills == 0
                ? 0
                : Math.round(
                    (matchedCount * 100.0) / totalSkills
                );

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("totalRequiredSkills", totalSkills);
        result.put("matchedCount", matchedCount);
        result.put("missingCount", missingSkills.size());
        result.put("matchedSkills", matchedSkills);
        result.put("missingSkills", missingSkills);
        result.put("matchPercentage", percentage);

        return result;
    }
}
