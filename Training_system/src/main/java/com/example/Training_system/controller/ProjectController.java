package com.example.Training_system.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.Training_system.entity.Project;
import com.example.Training_system.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/trainer/project")
@RequiredArgsConstructor
public class ProjectController {

	@Autowired
    private ProjectService service;

    // ✅ Create Project
    @PostMapping("/{batchId}")
    public Project create(@PathVariable Long batchId,
                          @RequestBody Project p) {
        return service.create(batchId, p);
    }

    // ✅ Get Projects
    @GetMapping("/{batchId}")
    public List<Project> get(@PathVariable Long batchId) {
        return service.getByBatch(batchId);
    }

    // ✅ Update Status
    @PutMapping("/{id}/status")
    public Project updateStatus(@PathVariable Long id,
                                @RequestParam String status) {
        return service.updateStatus(id, status);
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}