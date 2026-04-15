package com.example.Training_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Training_system.entity.MockInterview;

public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {

    List<MockInterview> findByBatchId(Long batchId);
}