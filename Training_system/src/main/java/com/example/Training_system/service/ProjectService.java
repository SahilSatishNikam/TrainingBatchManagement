package com.example.Training_system.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Training_system.entity.Batch;
import com.example.Training_system.entity.Project;
import com.example.Training_system.repository.BatchRepository;
import com.example.Training_system.repository.ProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

	@Autowired
    private ProjectRepository repo;
	
	@Autowired
    private BatchRepository batchRepo;

    // ✅ Create
    public Project create(Long batchId, Project p) {

        Batch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        p.setBatch(batch);
        p.setStatus("ONGOING");

        return repo.save(p);
    }

    // ✅ Get by batch
    public List<Project> getByBatch(Long batchId) {
        return repo.findByBatchId(batchId);
    }

    // ✅ Update status
    public Project updateStatus(Long id, String status) {

        Project p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        p.setStatus(status);

        return repo.save(p);
    }

    // ✅ Delete
    public void delete(Long id) {
        repo.deleteById(id);
    }
}