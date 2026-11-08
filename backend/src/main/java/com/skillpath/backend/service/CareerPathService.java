
package com.skillpath.backend.service;

import com.skillpath.backend.entity.CareerPath;
import com.skillpath.backend.repository.CareerPathRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareerPathService {

    private final CareerPathRepository careerPathRepository;

    public CareerPathService(CareerPathRepository careerPathRepository) {
        this.careerPathRepository = careerPathRepository;
    }

    // GET ALL CAREER PATHS
    public List<CareerPath> getAllCareerPaths() {
        return careerPathRepository.findAll();
    }

    // GET CAREER PATH BY ID
    public CareerPath getCareerPathById(Integer id) {
        return careerPathRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Career path not found"));
    }
}
