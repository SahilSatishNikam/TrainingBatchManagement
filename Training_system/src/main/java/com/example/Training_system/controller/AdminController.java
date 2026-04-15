package com.example.Training_system.controller;


import com.example.Training_system.dto.UserRequestDTO;
import com.example.Training_system.entity.Batch;
import com.example.Training_system.service.BatchService;
import com.example.Training_system.service.NotificationService;
import com.example.Training_system.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private UserService service;

    @Autowired
    private BatchService batchService;
    
    @Autowired
    private NotificationService notificationService;

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {

        Map<String, Object> data = new HashMap<>();

        List<Batch> allBatches = batchService.getAllBatchEntities();

        List<Batch> ongoing = allBatches.stream()
                .filter(b -> "ONGOING".equalsIgnoreCase(b.getStatus()))
                .toList();

        data.put("totalTrainers", service.getAllTrainerDTOs().size());
        data.put("totalBatches", allBatches.size());
        data.put("activeBatches", ongoing.size());

        data.put("completedBatches",
                allBatches.stream()
                        .filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus()))
                        .count()
        );

        data.put("batchList", ongoing.stream().map(b -> {
            Map<String, Object> m = new HashMap<>();

            m.put("id", b.getId());
            m.put("batchName", b.getBatchName());

            m.put("trainerName",
                    b.getTrainer() != null
                            ? b.getTrainer().getName() + " " + b.getTrainer().getLastName()
                            : "-"
            );

            m.put("startDate", b.getStartDate());
            m.put("endDate", b.getEndDate());
            m.put("progress", b.getProgressPercentage());

            return m;
        }).toList());

        return data;
    }

    // ================= TRAINERS =================
    @PostMapping("/create-trainer")
    public ResponseEntity<?> createTrainer(
            @Valid @ModelAttribute UserRequestDTO dto,
            @RequestParam("photo") MultipartFile photo
    ) throws Exception {

        String fileName = null;

        if (photo != null && !photo.isEmpty()) {
            fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();

            Path path = Path.of("uploads", fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, photo.getBytes());
        }

        return ResponseEntity.ok(service.createTrainer(dto, fileName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrainer(
            @PathVariable Long id,
            @ModelAttribute UserRequestDTO dto,
            @RequestParam(required = false) MultipartFile photo
    ) throws Exception {

        String fileName = null;

        if (photo != null && !photo.isEmpty()) {
            fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();

            Path path = Path.of("uploads", fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, photo.getBytes());
        }

        return ResponseEntity.ok(service.updateTrainer(id, dto, fileName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainer(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", service.deleteTrainer(id)));
    }

    @GetMapping("/trainers")
    public ResponseEntity<?> getAllTrainers() {
        return ResponseEntity.ok(service.getAllTrainerDTOs());
    }
}