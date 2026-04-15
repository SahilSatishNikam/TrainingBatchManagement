package com.example.Training_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.Training_system.entity.MockInterview;
import com.example.Training_system.service.MockInterviewService;

@RestController
@RequestMapping("/trainer/mock")
public class MockInterviewController {

    @Autowired
    private MockInterviewService service;

    // ✅ Create
    @PostMapping("/{batchId}")
    public MockInterview create(@PathVariable Long batchId,
                                @RequestBody MockInterview m) {
        return service.create(batchId, m);
    }

    // ✅ Get
    @GetMapping("/batch/{batchId}")
    public List<MockInterview> get(@PathVariable Long batchId) {
        return service.getByBatch(batchId);
    }

    // ✅ Update
    @PutMapping("/{id}")
    public MockInterview update(@PathVariable Long id,
                                @RequestBody MockInterview m) {
        return service.update(id, m);
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}/complete")
    public MockInterview completeMock(
            @PathVariable Long id,
            @RequestBody MockInterview m) {

        return service.completeMock(id, m);
    }
}