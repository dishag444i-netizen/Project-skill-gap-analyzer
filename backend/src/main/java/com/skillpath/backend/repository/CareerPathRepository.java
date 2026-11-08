
package com.skillpath.backend.repository;

import com.skillpath.backend.entity.CareerPath;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerPathRepository
        extends JpaRepository<CareerPath, Integer> {
}
