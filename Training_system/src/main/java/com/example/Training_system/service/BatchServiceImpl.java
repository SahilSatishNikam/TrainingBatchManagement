package com.example.Training_system.service;
import com.example.Training_system.dto.BatchDTO;
import com.example.Training_system.entity.Batch;
import com.example.Training_system.entity.User;
import com.example.Training_system.repository.BatchRepository;
import com.example.Training_system.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class  BatchServiceImpl implements BatchService {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private UserRepository userRepository;
    
 
    @Autowired
    private NotificationService notificationService;

    // ✅ CREATE BATCH
    @Override
    public Batch createBatch(Batch batch) {

        User trainer = userRepository.findById(batch.getTrainer().getId())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        batch.setTrainer(trainer);
        batch.setStatus("UPCOMING");

        if (batch.getCompletedDays() == 0) {
            batch.setCompletedDays(0);
        }

        if (batch.getTotalDays() <= 0) {
            throw new RuntimeException("Total days must be greater than 0");
        }

        Batch savedBatch = batchRepository.save(batch);

        // ✅ SEND NOTIFICATION (CORRECT PLACE)
        notificationService.sendToAll("📢 New batch created: " + savedBatch.getBatchName());

        return savedBatch;
    }
   
    @Override
    public List<BatchDTO> getAllBatches() {
        List<Batch> batches = batchRepository.findAll();

        return batches.stream().map(batch -> {

            BatchDTO dto = new BatchDTO();

            dto.setId(batch.getId());
            dto.setBatchName(batch.getBatchName());
            dto.setStartDate(batch.getStartDate().toString());
            dto.setEndDate(batch.getEndDate().toString());
            dto.setStatus(batch.getStatus());
            dto.setProgress(batch.getProgressPercentage());

            // ✅ IMPORTANT FIX (THIS LINE)
            if (batch.getTrainer() != null) {
                dto.setTrainerName(
                    batch.getTrainer().getName() + " " +
                    (batch.getTrainer().getLastName() != null ? batch.getTrainer().getLastName() : "")
                );
                dto.setTrainerId(batch.getTrainer().getId()); // for edit
            } else {
                dto.setTrainerName("-");
            }

            return dto;

        }).toList();
    }

    // ✅ UPDATE BATCH (ADMIN ONLY)
    @Override
    public Batch updateBatch(Long id, Batch batch) {

        Batch existingBatch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        // ✅ Update basic fields
        existingBatch.setBatchName(batch.getBatchName());
        existingBatch.setStartDate(batch.getStartDate());
        existingBatch.setEndDate(batch.getEndDate());
        existingBatch.setStatus(batch.getStatus());
        existingBatch.setTotalDays(batch.getTotalDays());
        existingBatch.setCompletedDays(batch.getCompletedDays());

        // ✅ FIX: Proper trainer assignment
        if (batch.getTrainer() != null && batch.getTrainer().getId() != null) {

            User trainer = userRepository.findById(batch.getTrainer().getId())
                    .orElseThrow(() -> new RuntimeException("Trainer not found"));

            existingBatch.setTrainer(trainer);

        } else {
            // Optional: clear trainer if not selected
            existingBatch.setTrainer(null);
        }

        return batchRepository.save(existingBatch);
    }

    // ✅ DELETE BATCH
    @Override
    public void deleteBatch(Long id) {
        batchRepository.deleteById(id);
    }

    @Override
    public Batch updateProgress(Long id, int days, Authentication auth) {

        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        User trainer = userRepository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        if (!batch.getTrainer().getId().equals(trainer.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        if (days < 0) {
            throw new RuntimeException("Days cannot be negative");
        }

        if (batch.getTotalDays() <= 0) {
            throw new RuntimeException("Total days not set");
        }

        if (days > batch.getTotalDays()) {
            throw new RuntimeException("Completed days cannot exceed total days");
        }

        // ✅ UPDATE
        batch.setCompletedDays(days);

        if (days == 0) {
            batch.setStatus("UPCOMING");
        } else if (days < batch.getTotalDays()) {
            batch.setStatus("ONGOING");
        } else {
            batch.setStatus("COMPLETED");
        }

        Batch updatedBatch = batchRepository.save(batch);

        // ✅ ONLY ONE NOTIFICATION HERE
        String msg = "📊 " + updatedBatch.getBatchName() +
                " progress updated: " +
                updatedBatch.getCompletedDays() + "/" +
                updatedBatch.getTotalDays() +
                " (" + updatedBatch.getProgressPercentage() + "%)";

        System.out.println("🔥 Sending Notification: " + msg);

        notificationService.sendToAll(msg);

        return updatedBatch;
    }
    // ✅ SECURE GET BATCH (IMPORTANT FIX)
    public Batch getBatchByIdForTrainer(Long id, Authentication auth) {

        String email = auth.getName();

        User trainer = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        return batchRepository
                .findByIdAndTrainerId(id, trainer.getId())
                .orElseThrow(() -> new RuntimeException("Batch not found or not assigned"));
    }

    // ================= DTO =================

    private BatchDTO convertToDTO(Batch batch) {

        BatchDTO dto = new BatchDTO();

        dto.setId(batch.getId()); // ✅ IMPORTANT

        dto.setBatchName(batch.getBatchName());

        if (batch.getTrainer() != null) {
            dto.setTrainerName(
                    batch.getTrainer().getName() + " " + batch.getTrainer().getLastName()
            );
            dto.setTrainerId(batch.getTrainer().getId()); // ✅ IMPORTANT
        }

        dto.setStartDate(batch.getStartDate().toString());
        dto.setEndDate(batch.getEndDate().toString());
        dto.setProgress(batch.getProgressPercentage());

        dto.setStatus(batch.getStatus()); // ✅ FIX STATUS

        return dto;
    }

    // ✅ YEAR
    @Override
    public List<BatchDTO> getCompletedBatchesByYear(int year) {

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        return batchRepository.findCompletedBetweenDates(start, end)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ✅ MONTH
    @Override
    public List<BatchDTO> getCompletedBatchesByMonth(int month, int year) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return batchRepository.findCompletedBetweenDates(start, end)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Override
    public List<Batch> getAllBatchEntities() {
        return batchRepository.findAll();
    }
}