package com.example.Training_system.controller;

import com.example.Training_system.dto.BatchDTO;
import com.example.Training_system.service.BatchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/batches/history")
@CrossOrigin("*")
public class BatchHistoryController {

    @Autowired
    private BatchService batchService;

    // ✅ YEAR FILTER
    @GetMapping("/year/{year}")
    public List<BatchDTO> getByYear(@PathVariable int year) {
        return batchService.getCompletedBatchesByYear(year);
    }

    // ✅ MONTH + YEAR FILTER
    @GetMapping("/month")
    public List<BatchDTO> getByMonth(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return batchService.getCompletedBatchesByMonth(month, year);
    }
}