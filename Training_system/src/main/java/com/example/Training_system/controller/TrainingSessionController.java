package com.example.Training_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.example.Training_system.entity.TrainingSession;
import com.example.Training_system.service.TrainingSessionService;

import java.util.List;

@RestController
@RequestMapping("/trainer/session")
@RequiredArgsConstructor
public class TrainingSessionController {

	@Autowired
    private TrainingSessionService service;

    // ✅ Create Session
    @PostMapping("/{batchId}")
    public TrainingSession create(@PathVariable Long batchId,
                                  @RequestBody TrainingSession session) {
        return service.createSession(batchId, session);
    }

    // ✅ Get Sessions by Batch
    @GetMapping("/{batchId}")
    public List<TrainingSession> getSessions(@PathVariable Integer batchId) {
        return service.getSessions(batchId);
    }

    // ✅ Update Status
    @PutMapping("/{id}/status")
    public TrainingSession updateStatus(@PathVariable Integer id,
                                        @RequestParam String status) {
        return service.updateStatus(id, status);
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteSession(id);
    }
}