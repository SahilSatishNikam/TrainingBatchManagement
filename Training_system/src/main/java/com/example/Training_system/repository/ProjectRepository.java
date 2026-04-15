package com.example.Training_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Training_system.entity.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ✅ Get all projects by batch
    List<Project> findByBatchId(Long batchId);

}