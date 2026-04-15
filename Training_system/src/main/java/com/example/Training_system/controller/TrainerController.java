package com.example.Training_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.Training_system.dto.ProgressRequest;
import com.example.Training_system.entity.Batch;
import com.example.Training_system.entity.User;
import com.example.Training_system.repository.BatchRepository;
import com.example.Training_system.repository.UserRepository;
import com.example.Training_system.service.BatchService;

@RestController
@RequestMapping("/trainer")
@CrossOrigin("*")
public class TrainerController {

    @Autowired
    private BatchRepository batchRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BatchService batchService;

    // ✅ MY BATCHES
    @GetMapping("/my-batches")
    public List<Batch> getMyBatches(Authentication auth) {

        User trainer = userRepo.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        return batchRepo.findByTrainerId(trainer.getId());
    }

    // ✅ GET SINGLE BATCH
    @GetMapping("/batch/{id}")
    public Batch getBatch(@PathVariable Long id, Authentication auth) {
        return batchService.getBatchByIdForTrainer(id, auth);
    }

    // ✅ UPDATE PROGRESS (NO DUPLICATE NOTIFICATION HERE)
    @PutMapping("/batch/{id}/progress")
    public Batch updateProgress(
            @PathVariable Long id,
            @RequestBody ProgressRequest request,
            Authentication auth) {

        return batchService.updateProgress(id, request.getDays(), auth);
    }

    // ✅ CREATE BATCH
    @PostMapping("/batch")
    public Batch createBatchByTrainer(@RequestBody Batch batch, Authentication auth) {

        User trainer = userRepo.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        batch.setTrainer(trainer);

        if (batch.getTotalDays() <= 0) {
            batch.setTotalDays(250);
        }

        batch.setCompletedDays(0);
        batch.setStatus("UPCOMING");

        return batchRepo.save(batch);
    }
}