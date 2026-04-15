package com.example.Training_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Training_system.entity.Batch;
import com.example.Training_system.entity.Practical;
import com.example.Training_system.repository.BatchRepository;
import com.example.Training_system.repository.PracticalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PracticalService {

    @Autowired
    private PracticalRepository repo;

    @Autowired
    private BatchRepository batchRepo;

    // ✅ Create Practical
    public Practical create(Long batchId, Practical p) {

        Batch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        p.setBatch(batch);
        p.setStatus("ASSIGNED");
        p.setAssignedDate(java.time.LocalDate.now());

        return repo.save(p);
    }

    // ✅ Get Practicals by Batch
    public List<Practical> getByBatch(Long batchId) {
        return repo.findByBatchId(batchId);
    }

    // ✅ Update Status with validation
    public Practical updateStatus(Long id, String status) {

        Practical p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Practical not found"));

        // ✅ Validate allowed values
        if (!List.of("ASSIGNED", "SUBMITTED", "COMPLETED").contains(status)) {
            throw new RuntimeException("Invalid status");
        }

        p.setStatus(status);

        // ✅ Auto set submission date when completed
        if (status.equals("COMPLETED")) {
            p.setSubmissionDate(java.time.LocalDate.now());
        }

        return repo.save(p);
    }

    // ✅ Delete
    public void delete(Long id) {

        if (!repo.existsById(id)) {
            throw new RuntimeException("Practical not found");
        }

        repo.deleteById(id);
    }
}