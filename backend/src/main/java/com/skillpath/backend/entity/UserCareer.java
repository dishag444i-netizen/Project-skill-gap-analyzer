
package com.skillpath.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_careers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "career_id"})
    }
)
public class UserCareer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "career_id", nullable = false)
    private Integer careerId;

    @Column(name = "selected_at")
    private LocalDateTime selectedAt;

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

    public LocalDateTime getSelectedAt() {
        return selectedAt;
    }

    public void setSelectedAt(LocalDateTime selectedAt) {
        this.selectedAt = selectedAt;
    }
}
