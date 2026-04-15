package com.example.Training_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Training_system.entity.Practical;

public interface PracticalRepository extends JpaRepository<Practical, Long> {
    List<Practical> findByBatchId(Long batchId);
}
