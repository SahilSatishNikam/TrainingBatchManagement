package com.example.Training_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Training_system.dto.BatchDTO;
import com.example.Training_system.dto.DashboardDTO;
import com.example.Training_system.entity.Batch;
import com.example.Training_system.entity.Role;
import com.example.Training_system.repository.BatchRepository;
import com.example.Training_system.repository.UserRepository;

@Service
public class DashboardService {

    @Autowired
    private BatchRepository batchRepo;

    @Autowired
    private UserRepository userRepo;

    public DashboardDTO getDashboardData() {

        DashboardDTO dto = new DashboardDTO();

        // ✅ COUNTS
        dto.setTotalTrainers(userRepo.countByRole(Role.TRAINER));
        dto.setTotalBatches(batchRepo.count());

        dto.setActiveBatches(batchRepo.countByStatus("ONGOING"));
        dto.setCompletedBatches(batchRepo.countByStatus("COMPLETED"));

        // ✅ LIST
        List<Batch> batches = batchRepo.findByStatus("ONGOING");

        List<BatchDTO> list = batches.stream().map(batch -> {

            BatchDTO b = new BatchDTO();

            b.setBatchName(batch.getBatchName());
            b.setTrainerName(batch.getTrainer().getName());
            b.setStartDate(batch.getStartDate().toString());
            b.setEndDate(batch.getEndDate().toString());
            b.setProgress(batch.getProgressPercentage());

            return b;

        }).toList();

        dto.setBatchList(list);

        return dto;
    }
}