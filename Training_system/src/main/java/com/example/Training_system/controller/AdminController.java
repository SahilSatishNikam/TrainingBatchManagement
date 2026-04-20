package com.example.Training_system.controller;

import com.example.Training_system.dto.UserRequestDTO;
import com.example.Training_system.entity.Batch;
import com.example.Training_system.service.BatchService;
import com.example.Training_system.service.NotificationService;
import com.example.Training_system.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

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
    public ResponseEntity<?> getDashboard() {

        List<Batch> allBatches = batchService.getAllBatchEntities();

        List<Batch> ongoing = allBatches.stream()
                .filter(b -> "ONGOING".equalsIgnoreCase(b.getStatus()))
                .toList();

        Map<String, Object> data = new HashMap<>();

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

        return ResponseEntity.ok(data);
    }

    // ================= CREATE TRAINER =================
    @PostMapping("/create-trainer")
    public ResponseEntity<?> createTrainer(
            @ModelAttribute UserRequestDTO dto,
            @RequestParam(required = false) MultipartFile photo
    ) {
        try {
            String fileName = savePhoto(photo);
            return ResponseEntity.ok(service.createTrainer(dto, fileName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Create failed: " + e.getMessage());
        }
    }

    // ================= UPDATE TRAINER =================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrainer(
            @PathVariable Long id,
            @ModelAttribute UserRequestDTO dto,
            @RequestParam(required = false) MultipartFile photo
    ) {
        try {
            String fileName = savePhoto(photo);
            return ResponseEntity.ok(service.updateTrainer(id, dto, fileName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    // ================= DELETE TRAINER =================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainer(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
                "message", service.deleteTrainer(id)
        ));
    }

    // ================= GET ALL TRAINERS =================
    @GetMapping("/trainers")
    public ResponseEntity<?> getAllTrainers() {
        return ResponseEntity.ok(service.getAllTrainerDTOs());
    }

    // ================= FILE UPLOAD (COMMON METHOD) =================
    private String savePhoto(MultipartFile photo) throws Exception {

        if (photo == null || photo.isEmpty()) {
            return null;
        }

        if (!photo.getContentType().startsWith("image/")) {
            throw new RuntimeException("Only image files allowed");
        }

        String fileName = System.currentTimeMillis() + "_" +
                Objects.requireNonNull(photo.getOriginalFilename())
                        .replaceAll("\\s+", "_");

        Path uploadPath = Paths.get("uploads");
        Files.createDirectories(uploadPath);

        Files.copy(photo.getInputStream(),
                uploadPath.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}