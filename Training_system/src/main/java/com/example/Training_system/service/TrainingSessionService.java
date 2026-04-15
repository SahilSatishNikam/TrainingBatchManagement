package com.example.Training_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.example.Training_system.entity.*;
import com.example.Training_system.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingSessionService {

	@Autowired
    private TrainingSessionRepository sessionRepo;
	
	@Autowired
    private BatchRepository batchRepo;

    // ✅ Create Session
    public TrainingSession createSession(Long batchId, TrainingSession session) {

        Batch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        session.setBatch(batch);
        session.setStatus("PLANNED");

        return sessionRepo.save(session);
    }

    // ✅ Get Sessions
    public List<TrainingSession> getSessions(Integer batchId) {
        return sessionRepo.findByBatchId(batchId);
    }

    // ✅ Update Status + Auto Progress
    public TrainingSession updateStatus(Integer id, String status) {

        TrainingSession session = sessionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 🔥 Prevent duplicate increment
        boolean alreadyCompleted = "COMPLETED".equalsIgnoreCase(session.getStatus());

        session.setStatus(status);

        if ("COMPLETED".equalsIgnoreCase(status) && !alreadyCompleted) {

            Batch batch = session.getBatch();

            batch.setCompletedDays(batch.getCompletedDays() + 1);

            batchRepo.save(batch); // ✅ update progress
        }

        return sessionRepo.save(session);
    }

    // ✅ Delete
    public void deleteSession(Integer id) {
        sessionRepo.deleteById(id);
    }
}