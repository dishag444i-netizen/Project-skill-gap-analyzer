
package com.skillpath.backend.controller;

import com.skillpath.backend.entity.CareerPath;
import com.skillpath.backend.service.CareerPathService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/careers")
@CrossOrigin(origins = "http://localhost:5173")
public class CareerPathController {

    private final CareerPathService careerPathService;

    public CareerPathController(CareerPathService careerPathService) {
        this.careerPathService = careerPathService;
    }

    // GET ALL CAREER PATHS
    @GetMapping
    public ResponseEntity<List<CareerPath>> getAllCareers(
            Authentication authentication) {

        return ResponseEntity.ok(
                careerPathService.getAllCareerPaths());
    }

    // GET CAREER PATH BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CareerPath> getCareerById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                careerPathService.getCareerPathById(id));
    }
}
