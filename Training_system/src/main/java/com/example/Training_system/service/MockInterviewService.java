package com.example.Training_system.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Training_system.entity.Batch;
import com.example.Training_system.entity.MockInterview;
import com.example.Training_system.repository.BatchRepository;
import com.example.Training_system.repository.MockInterviewRepository;

@Service
public class MockInterviewService {

    @Autowired
    private MockInterviewRepository repo;

    @Autowired
    private BatchRepository batchRepo;

    // ✅ Create
    public MockInterview create(Long batchId, MockInterview m) {
        Batch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        m.setBatch(batch);
        m.setStatus("SCHEDULED");

        if (m.getInterviewDate() == null) {
            m.setInterviewDate(LocalDate.now());
        }

        return repo.save(m);
    }

    // ✅ Get by batch
    public List<MockInterview> getByBatch(Long batchId) {
        return repo.findByBatchId(batchId);
    }

    // ✅ Update status + feedback
    public MockInterview update(Long id, MockInterview updated) {
        MockInterview m = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        m.setStatus(updated.getStatus());
        m.setFeedback(updated.getFeedback());
        m.setScore(updated.getScore());

        return repo.save(m);
    }

    // ✅ Delete
    public void delete(Long id) {
        repo.deleteById(id);
    }
    
    public MockInterview completeMock(Long id, MockInterview input) {

        MockInterview m = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mock not found"));

        m.setStatus("COMPLETED");
        m.setScore(input.getScore());
        m.setFeedback(input.getFeedback());

        return repo.save(m);
    }
}