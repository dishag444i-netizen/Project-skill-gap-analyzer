
package com.skillpath.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "learning_progress",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"user_id", "career_id"}
        )
    }
)
public class LearningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "career_id", nullable = false)
    private Integer careerId;

    @Column(name = "completed_topics")
    private Integer completedTopics = 0;

    @Column(name = "total_topics")
    private Integer totalTopics = 0;

    @Column(name = "progress_percentage")
    private Double progressPercentage = 0.0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCareerId() {
        return careerId;
    }

    public void setCareerId(Integer careerId) {
        this.careerId = careerId;
    }

    public Integer getCompletedTopics() {
        return completedTopics;
    }

    public void setCompletedTopics(Integer completedTopics) {
        this.completedTopics = completedTopics;
    }

    public Integer getTotalTopics() {
        return totalTopics;
    }

    public void setTotalTopics(Integer totalTopics) {
        this.totalTopics = totalTopics;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
