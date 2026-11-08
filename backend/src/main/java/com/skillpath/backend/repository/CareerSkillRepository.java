
package com.skillpath.backend.repository;

import com.skillpath.backend.entity.CareerSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerSkillRepository
        extends JpaRepository<CareerSkill, Integer> {

    List<CareerSkill> findByCareerId(Integer careerId);
}
