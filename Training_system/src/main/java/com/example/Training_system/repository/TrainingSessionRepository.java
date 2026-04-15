package com.example.Training_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Training_system.entity.TrainingSession;

import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Integer> {

    List<TrainingSession> findByBatchId(Integer batchId);
}