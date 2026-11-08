
package com.skillpath.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "career_skills")
public class CareerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "career_id", nullable = false)
    private Integer careerId;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCareerId() {
        return careerId;
    }

    public void setCareerId(Integer careerId) {
        this.careerId = careerId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}
