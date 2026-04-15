package com.example.Training_system.service;

import com.example.Training_system.dto.BatchDTO;
import com.example.Training_system.entity.Batch;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BatchService {

    Batch createBatch(Batch batch);

    List<BatchDTO> getAllBatches(); // ✅ DTO

    Batch updateBatch(Long id, Batch batch);

    void deleteBatch(Long id);

    List<Batch> getAllBatchEntities();
    
    Batch updateProgress(Long batchId, int newDays, Authentication auth);

    Batch getBatchByIdForTrainer(Long id, Authentication auth);

    List<BatchDTO> getCompletedBatchesByYear(int year);

    List<BatchDTO> getCompletedBatchesByMonth(int month, int year);
}