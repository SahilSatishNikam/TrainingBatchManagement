package com.example.Training_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Training_system.entity.Practical;
import com.example.Training_system.service.PracticalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trainer/practical")
@RequiredArgsConstructor
public class PracticalController {

	@Autowired
    private PracticalService service;

    @PostMapping("/{batchId}")
    public Practical create(@PathVariable Long batchId,
                            @RequestBody Practical p) {
        return service.create(batchId, p);
    }

    @GetMapping("/{batchId}")
    public List<Practical> get(@PathVariable Long batchId) {
        return service.getByBatch(batchId);
    }

    @PutMapping("/{id}/status")
    public Practical updateStatus(@PathVariable Long id,
                                 @RequestParam String status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}