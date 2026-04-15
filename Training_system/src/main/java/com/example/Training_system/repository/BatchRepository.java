package com.example.Training_system.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Training_system.entity.Batch;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    long countByStatus(String status);

    List<Batch> findByStatus(String status);

    // ✅ BEST METHOD (USE THIS ONLY)
    @Query("SELECT b FROM Batch b WHERE b.status = 'COMPLETED' AND b.endDate BETWEEN :startDate AND :endDate")
    List<Batch> findCompletedBetweenDates(LocalDate startDate, LocalDate endDate);

    // ✅ SECURITY METHOD (VERY IMPORTANT)
    @Query("SELECT b FROM Batch b WHERE b.id = :id AND b.trainer.id = :trainerId")
    Optional<Batch> findByIdAndTrainerId(Long id, Long trainerId);

    // ✅ OPTIONAL (used in trainer dashboard)
    List<Batch> findByTrainerId(Long trainerId);
}