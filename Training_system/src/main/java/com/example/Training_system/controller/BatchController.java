package com.example.Training_system.controller;

import com.example.Training_system.dto.BatchDTO;
import com.example.Training_system.dto.ProgressRequest;
import com.example.Training_system.entity.Batch;
import com.example.Training_system.service.BatchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @PostMapping("/batch")
    public Batch createBatch(@RequestBody Batch batch) {
        return batchService.createBatch(batch);
    }

    @GetMapping("/batches")
    public List<BatchDTO> getAllBatches() {
        return batchService.getAllBatches();
    }

    @PutMapping("/batch/{id}")
    public Batch updateBatch(@PathVariable Long id, @RequestBody Batch batch) {
        return batchService.updateBatch(id, batch);
    }

    @DeleteMapping("/batch/{id}")
    public String deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return "Deleted";
    }
    
    
    
}